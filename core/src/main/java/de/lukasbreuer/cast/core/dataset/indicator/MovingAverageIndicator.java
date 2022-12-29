package de.lukasbreuer.cast.core.dataset.indicator;

import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class MovingAverageIndicator extends ReviewIndicator {
  public static MovingAverageIndicator create(List<HistoryEntry> data) {
    var indicator = new MovingAverageIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private MovingAverageIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    var value = 0.0D;
    for (var i = 0; i < review; i++) {
      value += (prices().get(index - i) - prices().get(index - i - 1)) / prices().get(index - i - 1);
    }
    value /= review;
    return value;
  }
}
