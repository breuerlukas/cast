package de.lukasbreuer.stockalgorithm.deploy;

import com.google.inject.AbstractModule;
import de.lukasbreuer.stockalgorithm.core.CoreModule;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.investopedia.InvestopediaModule;
import de.lukasbreuer.stockalgorithm.deploy.model.ModelModule;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.PortfolioModule;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeModule;
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
