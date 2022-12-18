package de.lukasbreuer.stockalgorithm.train.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

import java.util.List;

public final class BullishPatternIndicator extends Indicator {
  public static BullishPatternIndicator create(List<HistoryEntry> data) {
    var indicator = new BullishPatternIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private BullishPatternIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index) {
    var entry = data().get(index);
    var candleLength = entry.high() - entry.low();
    var bodyLength = Math.abs(entry.open() - entry.close());
    var bodyCandleRelation = bodyLength / candleLength;
    var bodyUpperDistance = (entry.high() - entry.open()) / candleLength;
    return (bodyCandleRelation + bodyUpperDistance) / 2;
  }
}
