package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;
import java.util.stream.DoubleStream;

public final class MovingAverageIndicator extends ReviewIndicator {
  public static MovingAverageIndicator create(List<HistoryEntry> data) {
    var indicator = new MovingAverageIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private final double priceMaximum;

  private MovingAverageIndicator(List<HistoryEntry> data) {
    super(data);
    priceMaximum = prices().stream().mapToDouble(value -> value).max().getAsDouble();
  }

  @Override
  public double calculate(int index, int review) {
    return (calculateMovingAverage(prices(), index - review, review) / priceMaximum) * 100;
  }
}
