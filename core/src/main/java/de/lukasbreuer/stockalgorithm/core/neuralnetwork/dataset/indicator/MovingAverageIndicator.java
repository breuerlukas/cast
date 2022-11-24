package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;
import java.util.stream.DoubleStream;

public final class MovingAverageIndicator extends ReviewIndicator {
  private final double priceMaximum;

  private MovingAverageIndicator(List<HistoryEntry> data) {
    super(data);
    priceMaximum = ((DoubleStream) prices().stream()).max().getAsDouble();
  }

  @Override
  public double calculate(int index, int review) {
    return (calculateMovingAverage(prices(), index - review, review) / priceMaximum) * 100;
  }

  private double calculateMovingAverage(List<Double> prices, int skipDays, int period) {
    var value = 0.0D;
    for (var i = skipDays; i < (period + skipDays); i++) {
      if (i < 0 || i >= prices.size()) {
        continue;
      }
      value += prices.get(i);
    }
    value /= period;
    return value;
  }
}
