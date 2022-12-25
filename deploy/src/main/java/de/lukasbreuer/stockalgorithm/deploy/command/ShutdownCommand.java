package de.lukasbreuer.stockalgorithm.deploy.command;

import de.lukasbreuer.stockalgorithm.core.command.Command;

public final class ShutdownCommand extends Command {
  public static ShutdownCommand create() {
    return new ShutdownCommand();
  }

  private ShutdownCommand() {
    super("shutdown", new String[0]);
  }

  @Override
  public boolean execute(String[] arguments) {
    System.exit(-1);
    return true;
  }
}
