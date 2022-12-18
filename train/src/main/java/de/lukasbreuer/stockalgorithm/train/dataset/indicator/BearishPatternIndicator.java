package de.lukasbreuer.stockalgorithm.train.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public final class BearishPatternIndicator extends Indicator {
  public static BearishPatternIndicator create(List<HistoryEntry> data) {
    var indicator = new BearishPatternIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private BearishPatternIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index) {
    var entry = data().get(index);
    var candleLength = entry.high() - entry.low();
    var bodyLength = Math.abs(entry.open() - entry.close());
    var bodyCandleRelation = bodyLength / candleLength;
    var bodyLowerDistance = (entry.close() - entry.low()) / candleLength;
    return (bodyCandleRelation + bodyLowerDistance) / 2;
  }
}
