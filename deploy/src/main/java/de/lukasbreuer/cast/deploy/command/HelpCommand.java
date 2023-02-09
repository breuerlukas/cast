package de.lukasbreuer.cast.deploy.command;

import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;

public final class HelpCommand extends Command {
  public static HelpCommand create(Log log) {
    return new HelpCommand(log);
  }

  private HelpCommand(Log log) {
    super(log, "help", new String[] {"info", "command", "commands"}, new String[0]);
  }

  @Override
  public boolean execute(String[] arguments) {
    log().info("Commands: ");
    log().info("- portfolio");
    log().info("- model <stock>");
    log().info("- trades <stock>");
    log().info("- bankAccounts");
    log().info("- shutdown");
    return true;
  }
}
