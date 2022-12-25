package de.lukasbreuer.stockalgorithm.deploy.command;

import de.lukasbreuer.stockalgorithm.core.command.Command;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.Stock;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.StockCollection;

import java.util.List;

public final class PortfolioCommand extends Command {
  public static PortfolioCommand create(Log log, StockCollection stockCollection) {
    return new PortfolioCommand(log, stockCollection);
  }

  private final StockCollection stockCollection;

  private PortfolioCommand(Log log, StockCollection stockCollection) {
    super(log, "portfolio", new String[0]);
    this.stockCollection = stockCollection;
  }

  @Override
  public boolean execute(String[] arguments) {
    stockCollection.totalPortfolio().thenAccept(this::printPortfolio);
    return true;
  }

  private void printPortfolio(List<Stock> portfolio) {
    log().info("Portfolio: ");
    for (var stock : portfolio) {
      log().info(" - " + stock.formattedStockName());
    }
  }
}
