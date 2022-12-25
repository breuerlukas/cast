package de.lukasbreuer.stockalgorithm.core.log;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public final class Log extends Logger {
  private static final String LOG_PATH = "/logs/";

  public static Log create(String name) throws Exception {
    var consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(LogFormat.create(LogFormat.FormatType.CONSOLE));
    consoleHandler.setLevel(Level.ALL);
    var fileHandler = new FileHandler(System.getProperty("user.dir") + LOG_PATH +
      new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date()) + ".log");
    fileHandler.setFormatter(LogFormat.create(LogFormat.FormatType.FILE));
    var log = new Log(name);
    log.setLevel(Level.ALL);
    log.addHandler(consoleHandler);
    log.addHandler(fileHandler);
    Runtime.getRuntime().addShutdownHook(new Thread(log::close));
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

  public void close() {
    for (var handler : getHandlers()) {
      handler.close();
    }
  }
}
