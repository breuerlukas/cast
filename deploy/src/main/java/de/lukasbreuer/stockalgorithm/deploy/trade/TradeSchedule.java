package de.lukasbreuer.stockalgorithm.deploy.trade;

import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.Stock;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.StockCollection;
import de.lukasbreuer.stockalgorithm.deploy.trade.execution.TradeExecutionFactory;
import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(staticName = "create")
public final class TradeSchedule {
  private final Log log;
  private final StockCollection stockCollection;
  private final TradeExecutionFactory tradeExecutionFactory;
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> schedule;

  public void start() {
    var initialDelay = calculateInitialDelay();
    schedule = executorService.scheduleAtFixedRate(this::execute,
      initialDelay, 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
    log.info("Initialized trade schedule");
    log.info("First schedule execution in " +
      ((int) ((initialDelay / (1000 * 60 * 60)) % 24)) + " hour(s), " +
      ((int) ((initialDelay / (1000 * 60)) % 60)) + " minute(s), " +
      ((int) (initialDelay / 1000) % 60) + " second(s)");
  }

  private static final int MARKET_CLOSE_TIME = 22;

  private long calculateInitialDelay() {
    var currentDayDuration = calculateTimeTillDayTime(0, MARKET_CLOSE_TIME, 0);
    return currentDayDuration > 0 ? currentDayDuration :
      calculateTimeTillDayTime(1, MARKET_CLOSE_TIME, 0);
  }

  private long calculateTimeTillDayTime(int day, int hours, int minutes) {
    var calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hours);
    calendar.set(Calendar.MINUTE, minutes);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis() - System.currentTimeMillis();
  }

  private void execute() {
    stockCollection.totalPortfolio().thenAccept(this::execute);
  }

  private void execute(List<Stock> portfolio) {
    log.info("Start schedule execution");
    for (var stock : portfolio) {
      executeIndividual(stock);
    }
    log.info("Finished schedule execution");
  }

  private void executeIndividual(Stock stock) {
    var execution = tradeExecutionFactory.create(stock);
    execution.verify(TradeType.BUY);
    execution.verify(TradeType.SELL);
    log.info("Finished " + stock.formattedStockName() + " execution");
  }

  public void stop() {
    schedule.cancel(true);
    log.info("Stopped trade schedule");
  }
}
