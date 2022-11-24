package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public abstract class ReviewIndicator extends Indicator {
  protected ReviewIndicator(List<HistoryEntry> data) {
    super(data);
  }

  protected abstract double calculate(int index, int review);

  protected double calculate(int index) {
    return -1;
  }
}
