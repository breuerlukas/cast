package de.lukasbreuer.stockalgorithm.deploy;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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
  }

  @Provides
  @Singleton
  @Named("deployLog")
  Log provideDeployLog() {
    return Log.create("Deploy");
  }
}
