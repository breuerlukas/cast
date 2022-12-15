package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class TradePage extends Page {
  private static final String PAGE_URL = "https://www.investopedia.com/simulator/trade/stocks";

  public static TradePage create(WebDriver browser, String game, String stock) {
    return new TradePage(browser, PAGE_URL, game, stock);
  }

  private final String game;
  private final String stock;

  private TradePage(WebDriver browser, String url, String game, String stock) {
    super(browser, url);
    this.game = game;
    this.stock = stock;
  }

  @Override
  public void open() throws Exception {
    super.open();
    acceptRightsInformation();
    selectGame();
    selectStock();
    selectAction();
    selectQuantity();
    confirmTrade();
  }

  private void acceptRightsInformation() {
    browser().findElement(By.id("onetrust-accept-btn-handler")).click();
  }

  private void selectGame() {
    browser().findElement(By.className("portfolio-select")).click();
    browser().findElements(By.xpath("//*[text()[contains(., '" + game + "')]]")).stream()
      .filter(element -> element.getDomProperty("className").contains("v-list-item__title"))
      .findFirst().get().click();
  }

  private void selectStock() throws Exception {
    browser().findElement(By.id("input-75")).sendKeys(stock);
    Thread.sleep(2000);
    browser().findElements(By.xpath("//*[text()[contains(., '" + stock + "')]]")).stream()
      .filter(element -> element.getDomProperty("className").contains("symbol-name"))
      .findFirst().get().click();
  }

  private void selectAction() throws Exception {
    Thread.sleep(10000);
    browser().findElement(By.id("v-select__selections")).click();
    browser().findElements(By.xpath("//*[text()[contains(., '" + "BUY" + "')]]")).stream()
      .filter(element -> element.getDomProperty("className").contains("v-list-item__title"))
      .findFirst().get().click();
  }

  private void selectQuantity() {

  }

  private void confirmTrade() {

  }
}

/*
OLD IMPLEMENTATION OF TradePage

package de.lukasbreuer.stockstrategy.investopedia;

import com.jauntium.Browser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class TradePage implements Page {
    private final Browser browser;
    private final String stock;
    private final String game;
    private final TradingAction action;
    private final int quantity;
    @Getter
    private boolean successful;

    private static final String INVESTOPEDIA_TRADE_URL = "https://www.investopedia.com/simulator/trade/stocks";

    @Override
    public void open() throws Exception {
        browser.visit(INVESTOPEDIA_TRADE_URL);
        Thread.sleep(1000);
        acceptRightsInformation();
        selectCorrectGame();
        successful = selectStock();
        if (!successful) {
            return;
        }
        selectAction();
        selectQuantity();
        completeTrade();
    }

    private static final String RIGHTS_INFORMATION_ACCEPT_BUTTON = "<button id=\"onetrust-accept-btn-handler\" tabindex=\"0\">I Accept</button>";

    private void acceptRightsInformation() throws Exception {
        browser.doc.findFirst(RIGHTS_INFORMATION_ACCEPT_BUTTON).click();
        Thread.sleep(2000);
    }

    private static final String GAME_SELECTION_BUTTON = "<button data-v-6e1c584e=\"\" type=\"button\" class=\"menu-button justify-content-between v-btn v-btn--has-bg v-btn--tile theme--light v-size--default\" data-cy=\"portfolio-select\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\" style=\"width: 100%;\">";
    private static final String GAME_SELECTION_DROPDOWN_BUTTON = "<div data-v-6e1c584e=\"\" class=\"v-list-item__title text-truncate\">\n            %s\n          </div>";

    private void selectCorrectGame() throws Exception {
        browser.doc.findFirst(GAME_SELECTION_BUTTON).click();
        Thread.sleep(1000);
        browser.doc.findFirst(String.format(GAME_SELECTION_DROPDOWN_BUTTON, game)).click();
        Thread.sleep(1000);
    }

    private static final String STOCK_SELECTION_INPUT = "<input id=\"input-82\" placeholder=\"Look up Symbol/Company Name\" type=\"text\" autocomplete=\"off\">";
    private static final String STOCK_SELECTION_DROPDOWN_BUTTON = "<div tabindex=\"0\" class=\"v-list-item v-list-item--link theme--light\" aria-selected=\"false\" id=\"list-item-144-0\" role=\"option\"";

    private boolean selectStock() throws Exception {
        browser.doc.findFirst(STOCK_SELECTION_INPUT).sendKeys(stock);
        Thread.sleep(1000);
        if (browser.getSource().contains("is not a valid symbol.")) {
            return false;
        }
        browser.doc.findFirst(STOCK_SELECTION_DROPDOWN_BUTTON).click();
        Thread.sleep(1000);
        return true;
    }

    private static final String ACTION_SELECTION_DIV = "<div class=\"v-select__selections\"><div class=\"v-select__selection v-select__selection--comma\">";
    private static final String ACTION_SELECTION_DROPDOWN_DIV = "<div class=\"v-list-item__title\">%s</div>";

    private void selectAction() throws Exception {
        browser.doc.findFirst(ACTION_SELECTION_DIV).click();
        Thread.sleep(1000);
        browser.doc.findFirst(String.format(ACTION_SELECTION_DROPDOWN_DIV, action.key())).click();
        Thread.sleep(1000);
    }

    private static final String QUANTITY_MAX_BUTTON = "<button data-v-0212a269=\"\" type=\"button\" class=\"semi-bold text-capitalize ml-1 v-btn v-btn--text v-btn--tile theme--light elevation-0 v-size--default primary--text\" data-cy=\"quantity-button\" text=\"\" style=\"height: 3.5rem; width: auto;\">";
    private static final String QUANTITY_SELECTION_INPUT = "<input min=\"0\" max=\"999999\" data-cy=\"quantity-input\" id=\"input-98\" type=\"number\">";

    private void selectQuantity() throws Exception {
        if (action == TradingAction.SELL) {
            browser.doc.findFirst(QUANTITY_MAX_BUTTON).click();
        } else {
            browser.doc.findFirst(QUANTITY_SELECTION_INPUT).sendKeys(String.valueOf(quantity));
        }
        Thread.sleep(1000);
    }

    private static final String PREVIEW_ORDER_BUTTON = "<button data-v-0212a269=\"\" data-v-50347b5f=\"\" type=\"button\" class=\"semi-bold v-btn v-btn--has-bg v-btn--tile theme--light elevation-0 v-size--default primary\" data-cy=\"preview-button\" style=\"height: 3rem; width: 100%;\">";
    private static final String SUBMIT_ORDER_BUTTON = "<button data-v-0212a269=\"\" data-v-45708b02=\"\" type=\"button\" class=\"semi-bold v-btn v-btn--has-bg v-btn--tile theme--light elevation-0 v-size--default primary\" data-cy=\"submit-order-button\" style=\"height: 3rem; width: 100%;\">";

    private void completeTrade() throws Exception {
        browser.doc.findFirst(PREVIEW_ORDER_BUTTON).click();
        Thread.sleep(1000);
        browser.doc.findFirst(SUBMIT_ORDER_BUTTON).click();
        Thread.sleep(1000);
    }
}
 */