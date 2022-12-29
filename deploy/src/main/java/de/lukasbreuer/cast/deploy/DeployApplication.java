package de.lukasbreuer.cast.deploy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.lukasbreuer.cast.core.command.CommandRegistry;
import de.lukasbreuer.cast.core.command.CommandTask;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.command.ModelCommand;
import de.lukasbreuer.cast.deploy.command.PortfolioCommand;
import de.lukasbreuer.cast.deploy.command.ShutdownCommand;
import de.lukasbreuer.cast.deploy.command.TradeCommand;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import de.lukasbreuer.cast.deploy.trade.TradeSchedule;
import de.lukasbreuer.cast.deploy.trade.execution.TradeExecutionFactory;
import org.nd4j.linalg.factory.Nd4j;

public final class DeployApplication {
  public static void main(String[] args) {
    Nd4j.getRandom().setSeed(-1);
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
    commandRegistry.register(ModelCommand.create(log,
      injector.getInstance(ModelCollection.class)));
    commandRegistry.register(TradeCommand.create(log,
      injector.getInstance(TradeCollection.class)));
    commandRegistry.register(ShutdownCommand.create(log));
  }
}
