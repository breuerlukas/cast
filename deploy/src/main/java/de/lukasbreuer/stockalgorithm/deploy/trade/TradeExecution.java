package de.lukasbreuer.stockalgorithm.deploy.trade;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class TradeExecution {
  private final String stock;

  public void verify() {
    /*WebDriverManager.chromedriver().setup();
    var brower = WebDriverManager.chromedriver().create();
    brower.manage().window().maximize();
    LoginPage.create(brower, "DerCoder", "ZQxT4$V8B%Y#KgV").open();
    TradePage.create(brower, "Investopedia Trading Game", "AMZN",
      TradeType.SELL, 1).open();*/
  }
}
