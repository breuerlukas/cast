package de.lukasbreuer.stockalgorithm.core.log;


import java.util.logging.*;

public final class Log extends Logger {
  public static Log create(String name) {
    var consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(LogFormat.create());
    consoleHandler.setLevel(Level.ALL);
    var log = new Log(name);
    log.setLevel(Level.ALL);
    log.addHandler(consoleHandler);
    return log;
  }

  private Log(String name) {
    super(name, null);
  }

  @Override
  public void log(LogRecord record) {
    record.setMessage("[" + getName().toUpperCase() + "] " + record.getMessage());
    super.log(record);
  }
}
