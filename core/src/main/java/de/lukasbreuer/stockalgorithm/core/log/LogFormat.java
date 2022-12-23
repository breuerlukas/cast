package de.lukasbreuer.stockalgorithm.core.log;

import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@RequiredArgsConstructor(staticName = "create")
public final class LogFormat extends Formatter {
  private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  @Override
  public String format(LogRecord record) {
    var result = new StringBuilder();
    result.append(determineColor(record));
    result.append(buildPrefix(record));
    result.append(formatMessage(record));
    result.append("\n");
    return result.toString();
  }

  private static final String COLOR_WHITE = "\033[0;97m";
  private static final String COLOR_RED = "\033[0;91m";
  private static final String COLOR_DARK_RED = "\033[0;31m";
  private static final String COLOR_CYAN = "\033[0;36m";

  private String determineColor(LogRecord record) {
    if (record.getLevel() == Level.WARNING) {
      return COLOR_RED;
    }
    if (record.getLevel() == Level.SEVERE) {
      return COLOR_DARK_RED;
    }
    if (record.getLevel() == Level.FINE) {
      return COLOR_CYAN;
    }
    return COLOR_WHITE;
  }

  private String buildPrefix(LogRecord record) {
    var prefix = new StringBuilder();
    prefix.append("[");
    prefix.append(dateFormat.format(record.getMillis()));
    prefix.append(" - ");
    prefix.append(record.getLevel().getLocalizedName());
    prefix.append("]: ");
    return prefix.toString();
  }
}
