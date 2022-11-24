package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public final class RelativeStrengthIndicator extends ReviewIndicator {
  public static RelativeStrengthIndicator create(List<HistoryEntry> data) {
    var indicator = new RelativeStrengthIndicator(data);
    indicator.initialize();
    return indicator;
  }

  enum ChangeType {
    GAIN,
    LOSS;

    public boolean isGain() {
      return this == GAIN;
    }

    public boolean isLoss() {
      return this == LOSS;
    }
  }

  private RelativeStrengthIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    return 100 - (100 / (1 + (calculateAverageChange(prices(), index, review, ChangeType.GAIN) /
      calculateAverageChange(prices(), index, review, ChangeType.LOSS))));
  }

  private static double calculateAverageChange(
    List<Double> prices, int index, int review, ChangeType changeType
  ) {
    var accumulatedValue = 0D;
    var individualCount = 0D;
    for (var i = 0; i < review; i++) {
      var currentValue = prices.get(index - i);
      var previousValue = prices.get(index - i - 1);
      var change = currentValue - previousValue;
      if (changeType.isGain() ? change > 0 : change < 0) {
        accumulatedValue += change;
        individualCount++;
      }
    }
    if (individualCount == 0) {
      return 0;
    }
    return Math.abs(accumulatedValue / individualCount);
  }
}
