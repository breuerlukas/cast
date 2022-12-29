package de.lukasbreuer.cast.deploy;

import com.google.inject.AbstractModule;
import de.lukasbreuer.cast.core.CoreModule;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.investopedia.InvestopediaModule;
import de.lukasbreuer.cast.deploy.model.ModelModule;
import de.lukasbreuer.cast.deploy.portfolio.PortfolioModule;
import de.lukasbreuer.cast.deploy.trade.TradeModule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class DeployModule extends AbstractModule {
  @Override
  protected void configure() {
    install(CoreModule.create());
    install(PortfolioModule.create());
    install(ModelModule.create());
    install(TradeModule.create());
    install(InvestopediaModule.create());
    try {
      bind(Log.class).toInstance(Log.create("Deploy"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
