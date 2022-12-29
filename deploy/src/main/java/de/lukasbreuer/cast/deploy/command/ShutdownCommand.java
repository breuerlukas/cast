package de.lukasbreuer.cast.deploy.command;

import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;

public final class ShutdownCommand extends Command {
  public static ShutdownCommand create(Log log) {
    return new ShutdownCommand(log);
  }

  private ShutdownCommand(Log log) {
    super(log, "shutdown", new String[0]);
  }

  @Override
  public boolean execute(String[] arguments) {
    System.exit(-1);
    return true;
  }
}
