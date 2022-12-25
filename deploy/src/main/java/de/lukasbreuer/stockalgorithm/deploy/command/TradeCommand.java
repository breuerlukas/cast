package de.lukasbreuer.stockalgorithm.deploy.command;

import de.lukasbreuer.stockalgorithm.core.command.Command;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.trade.Trade;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeCollection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class TradeCommand extends Command {
  public static TradeCommand create(Log log, TradeCollection tradeCollection) {
    return new TradeCommand(log, tradeCollection);
  }

  private final TradeCollection tradeCollection;
  private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  private TradeCommand(Log log, TradeCollection tradeCollection) {
    super(log, "trades", new String[] {"stock"});
    this.tradeCollection = tradeCollection;
  }

  @Override
  public boolean execute(String[] arguments) {
    if (arguments.length != 1) {
      return false;
    }
    var stock = arguments[0].toUpperCase();
    tradeCollection.findByStock(stock).thenAccept(trades ->
      printTrades(stock, trades));
    return true;
  }

  private void printTrades(String stock, List<Trade> trades) {
    log().info("Trades (" + stock + "): ");
    if (trades.size() == 0) {
      log().info(" Empty");
      return;
    }
    for (var trade : trades) {
      log().info(" - " + dateFormat.format(new Date(trade.tradeTime())) + " / " +
        trade.tradeType() + " / " + trade.tradePrice());
    }
  }
}
