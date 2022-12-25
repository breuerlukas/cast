package de.lukasbreuer.stockalgorithm.deploy;

import com.google.inject.Guice;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.StockCollection;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeSchedule;
import de.lukasbreuer.stockalgorithm.deploy.trade.execution.TradeExecutionFactory;

public final class DeployApplication {
  public static void main(String[] args) {
    var injector = Guice.createInjector(DeployModule.create());
    var log = injector.getInstance(Log.class);
    var tradeSchedule = TradeSchedule.create(log, injector.getInstance(StockCollection.class),
      injector.getInstance(TradeExecutionFactory.class));
    tradeSchedule.start();
  }
}
