package de.lukasbreuer.cast.deploy.trade.execution;

import com.mongodb.client.result.InsertOneResult;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.core.trade.TradeType;
import de.lukasbreuer.cast.deploy.finance.BankAccountCollection;
import de.lukasbreuer.cast.deploy.investopedia.HomePage;
import de.lukasbreuer.cast.deploy.investopedia.LoginPage;
import de.lukasbreuer.cast.deploy.investopedia.TradePage;
import de.lukasbreuer.cast.deploy.model.Model;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.notification.Device;
import de.lukasbreuer.cast.deploy.notification.DeviceCollection;
import de.lukasbreuer.cast.deploy.notification.Notification;
import de.lukasbreuer.cast.deploy.notification.NotificationFactory;
import de.lukasbreuer.cast.deploy.portfolio.Stock;
import de.lukasbreuer.cast.deploy.trade.Trade;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "create")
public final class TradeExecution {
  public static void createAndInitialize(
    Log log, ModelCollection modelCollection, TradeCollection tradeCollection,
    BankAccountCollection bankAccountCollection, DeviceCollection deviceCollection,
    NotificationFactory notificationFactory, String investopediaUsername,
    String investopediaPassword, String investopediaGame, Stock stock,
    Consumer<TradeExecution> futureExecution
  ) {
    var execution = create(log, modelCollection, tradeCollection, bankAccountCollection,
      deviceCollection, notificationFactory, investopediaUsername,
      investopediaPassword, investopediaGame, stock);
    execution.initialize(() -> futureExecution.accept(execution));
  }

  public enum Action {
    TRADE,
    REMAIN
  }

  private final Log log;
  private final ModelCollection modelCollection;
  private final TradeCollection tradeCollection;
  private final BankAccountCollection bankAccountCollection;
  private final DeviceCollection deviceCollection;
  private final NotificationFactory notificationFactory;
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
    model.initialize(completed);
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
    var shouldExecute = TradeDecision.create(tradeType, latestBuyTrade, latestSellTrade,
      prediction, tradeType.isBuy() ? model.buyTradePredictionMinimum() :
        model.sellTradePredictionMinimum()).decide();
    if (shouldExecute) {
      log.fine("It has been decided that the " + tradeType + " of stock " +
        stock.formattedStockName() + " will be executed");
      log.info("These are the last 5 daily predictions " +
        Arrays.toString(Arrays.copyOfRange(prediction, prediction.length - 5, prediction.length)));
      var amount = 1;
      new Thread(() -> perform(tradeType, amount, () ->
        storeTrade(model, tradeType, amount, success ->
          actionFuture.accept(Action.TRADE)))).start();
      return;
    }
    log.info("It has been decided that the " + tradeType + " of stock " +
      stock.formattedStockName() + " will not be executed");
    actionFuture.accept(Action.REMAIN);
  }

  private void perform(TradeType tradeType, int amount, Runnable success) {
    try {
      var driver = WebDriverManager.chromedriver();
      driver.setup();
      configureBrowser(driver);
      var browser = driver.create();
      HomePage.create(browser).open();
      LoginPage.create(browser, investopediaUsername, investopediaPassword).open();
      TradePage.create(browser, investopediaGame, stock.formattedStockName(),
        tradeType, amount).open();
      sendNotification(tradeType);
      log.fine("Successfully performed " + stock.formattedStockName() + " trade");
      success.run();
    } catch (Exception exception) {
      log.severe("Performance of " + stock.formattedStockName() + " trade failed");
      exception.printStackTrace();
    }
  }

  private static final String BROWSER_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) " +
    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.5359.124 Safari/537.36";

  private void configureBrowser(WebDriverManager driver) {
    var options = new ChromeOptions();
    options.addArguments("--window-size=1920x1080");
    options.addArguments("--disable-extensions");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--no-sandbox");
    options.addArguments("--headless");
    options.addArguments("--user-agent=" + BROWSER_USER_AGENT);
    driver.capabilities(options);
  }

  private void sendNotification(TradeType tradeType) {
    var title = "Trade stock " + stock.formattedStockName();
    var body = "Stock is about to be " + (tradeType.isBuy() ? "bought" : "sold");
    var icon = "/img/logo.png";
    deviceCollection.allDevices(devices -> devices.forEach(device ->
      sendNotification(device, title, body, icon)));
  }

  private void sendNotification(
    Device device, String title, String body, String icon
  ) {
    var notification = notificationFactory.create(device, title, body, icon);
    try {
      var response = notification.send();
      if (response == Notification.Status.SUCCESSFUL) {
        return;
      }
      deviceCollection.removeDevice(device, success -> {});
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void storeTrade(
    Model model, TradeType tradeType, int amount, Consumer<InsertOneResult> response
  ) {
    var currentPrice = model.currentStockPrice();
    tradeCollection.addTrade(Trade.create(UUID.randomUUID(), stock.stockName(),
        tradeType, System.currentTimeMillis(), currentPrice), response);
    if (tradeType.isBuy()) {
      bankAccountCollection.firstBankAccount(bank -> bank.debit(currentPrice * amount));
      return;
    }
    bankAccountCollection.firstBankAccount(bank -> bank.deposit(currentPrice * amount));
  }
}
