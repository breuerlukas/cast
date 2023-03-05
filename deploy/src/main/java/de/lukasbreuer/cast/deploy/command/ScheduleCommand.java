package de.lukasbreuer.cast.deploy.command;

import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;

import java.util.Calendar;

public final class ScheduleCommand extends Command {
  public static ScheduleCommand create(Log log) {
    return new ScheduleCommand(log);
  }

  private ScheduleCommand(Log log) {
    super(log, "schedule", new String[] {"time", "delay"}, new String[0]);
  }

  private static final int MARKET_CLOSE_TIME = 22;

  @Override
  public boolean execute(String[] arguments) {
    log().info(formatInitialDelay(calculateScheduleDelay()));
    return true;
  }

  private long calculateScheduleDelay() {
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

  private String formatInitialDelay(long delay) {
    var result = new StringBuilder();
    result.append(((int) ((delay / (1000 * 60 * 60)) % 24)) + " hour(s), ");
    result.append(((int) ((delay / (1000 * 60)) % 60)) + " minute(s), ");
    result.append(((int) ((delay / 1000) % 60)) + " second(s)");
    return result.toString();
  }
}
