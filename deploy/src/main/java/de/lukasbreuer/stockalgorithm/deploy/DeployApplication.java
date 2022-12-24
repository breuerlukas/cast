package de.lukasbreuer.stockalgorithm.deploy;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.name.Names;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.StockCollection;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeSchedule;
import de.lukasbreuer.stockalgorithm.deploy.trade.execution.TradeExecutionFactory;

public final class DeployApplication {
  public static void main(String[] args) throws Exception {
    var injector = Guice.createInjector(DeployModule.create());
    var log = injector.getInstance(Key.get(Log.class, Names.named("deployLog")));
    var tradeSchedule = TradeSchedule.create(log, injector.getInstance(StockCollection.class),
      injector.getInstance(TradeExecutionFactory.class));
    tradeSchedule.start();
  }
}
