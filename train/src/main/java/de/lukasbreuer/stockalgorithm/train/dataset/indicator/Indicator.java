package de.lukasbreuer.stockalgorithm.train.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Indicator {
  @Getter(AccessLevel.PROTECTED)
  private final List<HistoryEntry> data;
  @Getter(AccessLevel.PROTECTED)
  private List<Double> prices;

  public void initialize() {
    prices = data.stream().map(HistoryEntry::close)
      .collect(Collectors.toList());
  }

  public abstract double calculate(int index);
}
