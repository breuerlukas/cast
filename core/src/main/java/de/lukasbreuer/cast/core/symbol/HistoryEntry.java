package de.lukasbreuer.cast.core.symbol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class HistoryEntry {
  private final long timeStep;
  private final double open;
  private final double close;
  private final double high;
  private final double low;
  private final double volume;
}
