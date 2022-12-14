package de.lukasbreuer.stockalgorithm.train.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public final class AverageRangeIndicator extends ReviewIndicator {
  public static AverageRangeIndicator create(List<HistoryEntry> data) {
    var indicator = new AverageRangeIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private AverageRangeIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    var accumulatedTR = 0D;
    for (var i = 0; i < review; i++) {
      accumulatedTR += calculateTrueRange(index - i);
    }
    return accumulatedTR / review;
  }

  private double calculateTrueRange(int currentIndex) {
    return Math.max(data().get(currentIndex).high() - data().get(currentIndex).low(),
      Math.max(Math.abs(data().get(currentIndex).high() - data().get(currentIndex - 1).close()),
        Math.abs(data().get(currentIndex).low() - data().get(currentIndex - 1).close())));
  }
}
