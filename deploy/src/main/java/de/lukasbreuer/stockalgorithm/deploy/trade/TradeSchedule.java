package de.lukasbreuer.stockalgorithm.deploy.trade;

import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(staticName = "create")
public final class TradeSchedule {
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> schedule;

  public void start() {
    schedule = executorService.scheduleAtFixedRate(this::execute,
      calculateInitialDelay(), 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS);
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

  //TODO: IMPLEMENT
  private void execute() {

  }

  public void stop() {
    schedule.cancel(true);
  }
}
