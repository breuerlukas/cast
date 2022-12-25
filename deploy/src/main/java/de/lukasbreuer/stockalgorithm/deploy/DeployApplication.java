package de.lukasbreuer.stockalgorithm.deploy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.lukasbreuer.stockalgorithm.core.command.CommandRegistry;
import de.lukasbreuer.stockalgorithm.core.command.CommandTask;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.command.PortfolioCommand;
import de.lukasbreuer.stockalgorithm.deploy.command.ShutdownCommand;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.StockCollection;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeSchedule;
import de.lukasbreuer.stockalgorithm.deploy.trade.execution.TradeExecutionFactory;

public final class DeployApplication {
  public static void main(String[] args) {
    var injector = Guice.createInjector(DeployModule.create());
    var log = injector.getInstance(Log.class);
    var commandRegistry = injector.getInstance(CommandRegistry.class);
    registerCommands(injector, log, commandRegistry);
    new Thread(() -> CommandTask.create(log, commandRegistry).start()).start();
    var tradeSchedule = TradeSchedule.create(log, injector.getInstance(StockCollection.class),
      injector.getInstance(TradeExecutionFactory.class));
    tradeSchedule.start();
  }

  private static void registerCommands(
    Injector injector, Log log, CommandRegistry commandRegistry
  ) {
    commandRegistry.register(PortfolioCommand.create(log,
      injector.getInstance(StockCollection.class)));
    commandRegistry.register(ShutdownCommand.create(log));
  }
}
