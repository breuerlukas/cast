package de.lukasbreuer.stockalgorithm.deploy.trade.execution;

import com.mongodb.client.result.InsertOneResult;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.deploy.investopedia.LoginPage;
import de.lukasbreuer.stockalgorithm.deploy.investopedia.TradePage;
import de.lukasbreuer.stockalgorithm.deploy.model.Model;
import de.lukasbreuer.stockalgorithm.deploy.model.ModelCollection;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.Stock;
import de.lukasbreuer.stockalgorithm.deploy.trade.Trade;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeCollection;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "create")
public final class TradeExecution {
  public static void createAndInitialize(
    Log log, ModelCollection modelCollection, TradeCollection tradeCollection,
    String investopediaUsername, String investopediaPassword,
    String investopediaGame, Stock stock, Consumer<TradeExecution> futureExecution
  ) {
    var execution = create(log, modelCollection, tradeCollection,
      investopediaUsername, investopediaPassword, investopediaGame, stock);
    execution.initialize(() -> futureExecution.accept(execution));
  }

  public enum Action {
    TRADE,
    REMAIN
  }

  private final Log log;
  private final ModelCollection modelCollection;
  private final TradeCollection tradeCollection;
  private final String investopediaUsername;
  private final String investopediaPassword;
  private final String investopediaGame;
  private final Stock stock;
  private Model model;

  public void initialize(Runnable completed) {
    modelCollection.findByStock(stock.stockName(),
      model -> initialize(model, completed));
  }

  private void initialize(Model model, Runnable completed) {
    this.model = model;
    model.initialize();
    completed.run();
  }

  public void verify(TradeType tradeType, Consumer<Action> actionFuture) {
    tradeCollection.findLatestByStock(stock.stockName(), TradeType.BUY,
      latestBuyTrade -> tradeCollection.findLatestByStock(stock.stockName(), TradeType.SELL,
        latestSellTrade -> verify(tradeType, latestBuyTrade, latestSellTrade, actionFuture)));
  }

  private static final int MODEL_PREDICTION_PERIOD = 20;

  private synchronized void verify(
    TradeType tradeType, Optional<Trade> latestBuyTrade,
    Optional<Trade> latestSellTrade, Consumer<Action> actionFuture
  ) {
    var prediction = model.predict(tradeType, MODEL_PREDICTION_PERIOD);
    var shouldExecute = TradeDecision.create(log, stock, tradeType, latestBuyTrade,
      latestSellTrade, prediction, tradeType.isBuy() ? model.buyTradePredictionMinimum() :
        model.sellTradePredictionMinimum()).decide();
    if (shouldExecute) {
      log.fine("It has been decided that the " + tradeType + " of stock " +
        stock.formattedStockName() + " will be executed");
      storeTrade(model, tradeType, success ->
        actionFuture.accept(Action.TRADE));
      /*new Thread(() -> perform(tradeType, 1, () ->
        storeTrade(model, tradeType, success ->
          actionFuture.accept(Action.TRADE)))).start();*/
      return;
    }
    log.info("It has been decided that the " + tradeType + " of stock " +
      stock.formattedStockName() + " will not be executed");
    actionFuture.accept(Action.REMAIN);
  }

  private void perform(TradeType tradeType, int amount, Runnable success) {
    try {
      WebDriverManager.chromedriver().setup();
      var browser = WebDriverManager.chromedriver().create();
      browser.manage().window().maximize();
      LoginPage.create(browser, investopediaUsername, investopediaPassword).open();
      TradePage.create(browser, investopediaGame, stock.formattedStockName(),
        tradeType, amount).open();
      log.fine("Successfully performed " + stock.formattedStockName() + " trade");
      success.run();
    } catch (Exception exception) {
      log.severe("Performance of " + stock.formattedStockName() + " trade failed");
      exception.printStackTrace();
    }
  }

  private void storeTrade(
    Model model, TradeType tradeType, Consumer<InsertOneResult> response
  ) {
    tradeCollection.addTrade(Trade.create(UUID.randomUUID(), stock.stockName(),
        tradeType, System.currentTimeMillis(), model.currentStockPrice()), response);
  }
}
