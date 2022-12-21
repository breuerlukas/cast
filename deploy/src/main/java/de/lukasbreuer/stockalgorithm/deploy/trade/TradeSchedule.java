package de.lukasbreuer.stockalgorithm.deploy.trade;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(staticName = "create")
public final class TradeSchedule {
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> schedule;

  //TODO: CALCULATE DURATION TILL CERTAIN DAY TIME IS REACHED
  public void start() {
    schedule = executorService.scheduleAtFixedRate(this::execute, 0, 24, TimeUnit.HOURS);
  }

  //TODO: IMPLEMENT
  private void execute() {

  }

  public void stop() {
    schedule.cancel(true);
  }
}
