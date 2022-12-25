package de.lukasbreuer.stockalgorithm.core.command;

import de.lukasbreuer.stockalgorithm.core.log.Log;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;

@RequiredArgsConstructor(staticName = "create")
public final class CommandTask {
  private final Log log;
  private final CommandRegistry commandRegistry;

  public void start() {
    var reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      monitorInput(reader);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void monitorInput(BufferedReader reader) throws Exception {
    var line = "";
    while((line = reader.readLine()) != null) {
      if(line.length() == 0) {
        continue;
      }
      superviseInput(line);
    }
  }

  private void superviseInput(String line) {
    var input = line.split(" ");
    var commandName = input[0];
    commandRegistry.findByName(commandName)
      .ifPresent(command -> executeCommand(command, line, input));
  }

  private void executeCommand(Command command, String line, String[] input) {
    var length = input.length - 1;
    var arguments = new String[length];
    System.arraycopy(input,1, arguments,0, length);
    try {
      log.fileLog(Level.INFO, "Entered command '" + line + "'");
      if (!command.execute(arguments)) {
        printOutSyntax(command);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void printOutSyntax(Command command) {
    var arguments = command.arguments();
    var syntax = new StringBuilder("Syntax: " + command.name() + " ");
    for (var argument : arguments) {
      syntax.append("<").append(argument).append("> ");
    }
    log.info(syntax.toString());
  }
}
