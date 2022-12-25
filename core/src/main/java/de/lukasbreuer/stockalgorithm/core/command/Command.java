package de.lukasbreuer.stockalgorithm.core.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Command {
  @Getter
  private final String name;
  @Getter
  private final String[] arguments;

  public abstract boolean execute(String[] arguments);
}
