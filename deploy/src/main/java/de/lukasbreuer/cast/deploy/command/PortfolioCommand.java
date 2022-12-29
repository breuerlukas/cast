package de.lukasbreuer.cast.deploy.command;

import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.portfolio.Stock;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;

import java.util.List;
import java.util.UUID;

public final class PortfolioCommand extends Command {
  public static PortfolioCommand create(Log log, StockCollection stockCollection) {
    return new PortfolioCommand(log, stockCollection);
  }

  private final StockCollection stockCollection;

  private PortfolioCommand(Log log, StockCollection stockCollection) {
    super(log, "portfolio", new String[] {"add <stock>", "remove <stock>"});
    this.stockCollection = stockCollection;
  }

  @Override
  public boolean execute(String[] arguments) {
    if (arguments.length == 0) {
      stockCollection.totalPortfolio(this::printPortfolio);
      return true;
    }
    if (arguments[0].equalsIgnoreCase("add")) {
      return executeAdd(arguments);
    }
    if (arguments[0].equalsIgnoreCase("remove")) {
      return executeRemove(arguments);
    }
    return false;
  }

  private boolean executeAdd(String[] arguments) {
    if (arguments.length != 2) {
      return false;
    }
    var stock = arguments[1].toUpperCase();
    stockCollection.addStock(Stock.create(UUID.randomUUID(), stock), success ->
      log().info("Stock " + stock + " has been successfully added to the portfolio"));
    return true;
  }

  private boolean executeRemove(String[] arguments) {
    if (arguments.length != 2) {
      return false;
    }
    var stock = arguments[1].toUpperCase();
    stockCollection.removeStock(stock, success ->
      log().info("Stock " + stock + " has been successfully removed from the portfolio"));
    return true;
  }

  private void printPortfolio(List<Stock> portfolio) {
    log().info("Portfolio: ");
    if (portfolio.size() == 0) {
      log().info(" Empty");
      return;
    }
    for (var stock : portfolio) {
      log().info(" - " + stock.formattedStockName());
    }
  }
}
