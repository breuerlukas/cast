package de.lukasbreuer.cast.deploy;

import de.lukasbreuer.cast.core.CoreModule;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.investopedia.InvestopediaModule;
import de.lukasbreuer.cast.deploy.model.ModelModule;
import de.lukasbreuer.cast.deploy.portfolio.PortfolioModule;
import de.lukasbreuer.cast.deploy.trade.TradeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DeployModule extends CoreModule {
  public static DeployModule create() {
    return new DeployModule();
  }

  @Override
  protected void configure() {
    super.configure();
    install(PortfolioModule.create());
    install(ModelModule.create());
    install(TradeModule.create());
    install(InvestopediaModule.create());
  }

  @Override
  protected void configureLog() {
    try {
      bind(Log.class).toInstance(Log.create("Deploy", "/logs/deploy/"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
