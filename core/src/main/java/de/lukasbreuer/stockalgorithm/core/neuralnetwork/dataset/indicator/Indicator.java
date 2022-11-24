package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

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
  private final List<HistoryEntry> data;
  @Getter
  private List<Double> prices;

  public void initialize() {
    prices = data.stream().map(HistoryEntry::close)
      .collect(Collectors.toList());
  }

  protected abstract double calculate(int index);
}
