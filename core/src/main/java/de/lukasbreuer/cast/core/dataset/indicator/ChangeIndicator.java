package de.lukasbreuer.cast.core.dataset.indicator;

import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class ChangeIndicator extends ReviewIndicator {
  public static ChangeIndicator create(List<HistoryEntry> data) {
    var indicator = new ChangeIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private ChangeIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    return (prices().get(index - review) - prices().get(index - review - 1)) /
      prices().get(index - review - 1);
  }
}
