package de.lukasbreuer.cast.core.dataset.indicator;


import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class ExponentialMovingAverageIndicator extends ReviewIndicator {
  public static ExponentialMovingAverageIndicator create(List<HistoryEntry> data) {
    var indicator = new ExponentialMovingAverageIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private ExponentialMovingAverageIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    var multiplier = 2D / (review + 1);
    var lastEma = (prices().get(index) - prices().get(index - 1)) / prices().get(index - 1);
    for (var i = 0; i < review; i++) {
      var priceChange = (prices().get(index - i) - prices().get(index - i - 1)) / prices().get(index - i - 1);
      lastEma = (priceChange - lastEma) * multiplier + lastEma;
    }
    return lastEma;
  }
}
