package de.lukasbreuer.cast.deploy.investopedia;

import de.lukasbreuer.cast.core.trade.TradeType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class TradePage extends Page {
  private static final String PAGE_URL = "https://www.investopedia.com/simulator/trade/stocks";

  public static TradePage create(
    WebDriver browser, String game, String stock, TradeType tradeType,
    int quantity
  ) {
    return new TradePage(browser, PAGE_URL, game, stock, tradeType, quantity);
  }

  private final String game;
  private final String stock;
  private final TradeType tradeType;
  private final int quantity;

  private TradePage(
    WebDriver browser, String url, String game, String stock,
    TradeType tradeType, int quantity
  ) {
    super(browser, url);
    this.game = game;
    this.stock = stock;
    this.tradeType = tradeType;
    this.quantity = quantity;
  }

  @Override
  public void open() throws Exception {
    super.open();
    selectGame();
    selectStock();
    selectAction();
    selectQuantity();
    selectDuration();
    confirmTrade();
  }

  private void selectGame() throws Exception {
    browser().findElement(By.cssSelector("button[data-cy='portfolio-select']")).click();
    Thread.sleep(200);
    browser().findElements(By.xpath("//*[text()[contains(., '" + game + "')]]")).stream()
      .filter(element -> element.getDomProperty("className").contains("v-list-item__title"))
      .findFirst().get().click();
  }

  private void selectStock() throws Exception {
    browser().findElement(By.cssSelector(
      "input[placeholder='Look up Symbol/Company Name']")).sendKeys(stock);
    Thread.sleep(1000);
    browser().findElement(By.id("list-item-161-0")).click();
    Thread.sleep(200);
  }

  private void selectAction() throws Exception {
    browser().findElement(By.cssSelector("input[data-cy='action-select']"))
      .findElement(By.xpath("./..")).click();
    Thread.sleep(200);
    browser().findElement(By.id("list-item-190-" +
      calculateActionIndex(tradeType))).click();
  }

  private int calculateActionIndex(TradeType action) {
    if (action.isBuy()) {
      return 0;
    }
    if (action.isSell()) {
      return 1;
    }
    return -1;
  }

  private void selectQuantity() {
    browser().findElement(By.cssSelector("input[data-cy='quantity-input']"))
      .sendKeys(String.valueOf(quantity));
  }

  private void selectDuration() throws Exception {
    browser().findElement(By.cssSelector("input[data-cy='duration-select']"))
      .findElement(By.xpath("./..")).click();
    Thread.sleep(200);
    browser().findElement(By.id("list-item-198-1")).click();
  }

  private void confirmTrade() throws Exception {
    browser().findElement(By.cssSelector("button[data-cy='preview-button']")).click();
    Thread.sleep(1000);
    browser().findElement(By.cssSelector("button[data-cy='submit-order-button']")).click();
  }
}