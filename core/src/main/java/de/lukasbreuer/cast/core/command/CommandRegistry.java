package de.lukasbreuer.cast.core.command;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class CommandRegistry {
  private final Map<String, Command> commands = Maps.newHashMap();

  public void register(Command command) {
    commands.put(command.name(), command);
  }

  public void unregister(String name) {
    commands.remove(name);
  }

  public boolean exists(String name) {
    return commands.containsKey(name);
  }

  public Optional<Command> findByName(String name) {
    return Optional.ofNullable(commands.get(name));
  }
}
