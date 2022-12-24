package de.lukasbreuer.stockalgorithm.deploy.trade.execution;

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

@RequiredArgsConstructor(staticName = "create")
public final class TradeExecution {
  private final Log log;
  private final ModelCollection modelCollection;
  private final TradeCollection tradeCollection;
  private final String investopediaUsername;
  private final String investopediaPassword;
  private final String investopediaGame;

  private final Stock stock;

  public void verify(TradeType tradeType) {
    modelCollection.findByStock(stock.stockName())
      .thenAccept(model -> tradeCollection.findLatestByStock(stock.stockName(), tradeType)
        .thenAccept(latestTrade -> verify(model, tradeType, latestTrade)));
  }

  private static final int MODEL_PREDICTION_PERIOD = 20;

  private void verify(Model model, TradeType tradeType, Optional<Trade> latestTrade) {
    model.initialize();
    var prediction = model.predict(tradeType, MODEL_PREDICTION_PERIOD);
    var shouldExecute = TradeExecutionDecision.create(stock, tradeType,
      latestTrade, prediction).decide();
    if (shouldExecute) {
      new Thread(() -> perform(tradeType, 1, () ->
        storeTrade(model, tradeType))).start();
    }
  }

  private void perform(TradeType tradeType, int amount, Runnable success) {
    try {
      WebDriverManager.chromedriver().setup();
      var browser = WebDriverManager.chromedriver().create();
      browser.manage().window().maximize();
      LoginPage.create(browser, investopediaUsername, investopediaPassword).open();
      TradePage.create(browser, investopediaGame, stock.formattedStockName(),
        tradeType, amount).open();
      log.info("Successfully performed " + stock.formattedStockName() + " trade");
      success.run();
    } catch (Exception exception) {
      log.severe("Performance of " + stock.formattedStockName() + " trade failed");
      exception.printStackTrace();
    }
  }

  private void storeTrade(Model model, TradeType tradeType) {
    tradeCollection.addTrade(Trade.create(UUID.randomUUID(), stock.stockName(),
        tradeType, System.currentTimeMillis(), model.currentStockPrice()),
      result -> log.info("Successfully stored " +
        stock.formattedStockName() + " trade"));
  }
}
