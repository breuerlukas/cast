package de.lukasbreuer.stockalgorithm.deploy;

import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.deploy.investopedia.LoginPage;
import de.lukasbreuer.stockalgorithm.deploy.investopedia.TradePage;
import io.github.bonigarcia.wdm.WebDriverManager;

public final class DeployApplication {
  public static void main(String[] args) throws Exception {
    WebDriverManager.chromedriver().setup();
    var brower = WebDriverManager.chromedriver().create();
    brower.manage().window().maximize();
    LoginPage.create(brower, "DerCoder", "ZQxT4$V8B%Y#KgV").open();
    TradePage.create(brower, "Investopedia Trading Game", "AMZN",
      TradeType.SELL, 1).open();
  }
}
