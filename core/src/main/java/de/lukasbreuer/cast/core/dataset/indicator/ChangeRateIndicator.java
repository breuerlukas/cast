package de.lukasbreuer.cast.core.dataset.indicator;

import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class ChangeRateIndicator extends ReviewIndicator {
  public static ChangeRateIndicator create(List<HistoryEntry> data) {
    var indicator = new ChangeRateIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private ChangeRateIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    return (prices().get(index) - prices().get(index - review)) /
      prices().get(index - review);
  }
}
