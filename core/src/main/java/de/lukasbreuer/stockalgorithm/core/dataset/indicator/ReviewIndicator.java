package de.lukasbreuer.stockalgorithm.core.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public abstract class ReviewIndicator extends Indicator {
  protected ReviewIndicator(List<HistoryEntry> data) {
    super(data);
  }

  public abstract double calculate(int index, int review);

  public double calculate(int index) {
    return -1;
  }
}
