package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public final class StochasticOscillatorIndicator extends ReviewIndicator {
  public static StochasticOscillatorIndicator create(List<HistoryEntry> data) {
    var indicator = new StochasticOscillatorIndicator(data);
    indicator.initialize();
    return indicator;
  }

  enum ExtremeType {
    HIGH,
    LOW;

    public boolean isHigh() {
      return this == HIGH;
    }

    public boolean isLow() {
      return this == LOW;
    }
  }

  private StochasticOscillatorIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    var lowestLow = findExtreme(index, review, ExtremeType.LOW);
    var highestHigh = findExtreme(index, review, ExtremeType.HIGH);
    return ((data().get(index).close() - lowestLow) / (highestHigh - lowestLow)) * 100;
  }

  private double findExtreme(int index, int review, ExtremeType type) {
    var extreme = type.isHigh() ? 0 : Double.MAX_VALUE;
    for (var i = 0; i < review; i++) {
      var entry = data().get(index - i);
      var value = type.isHigh() ? entry.high() : entry.low();
      if (type.isHigh() ? value > extreme : value < extreme) {
        extreme = value;
      }
    }
    return extreme;
  }
}
