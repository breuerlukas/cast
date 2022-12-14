package de.lukasbreuer.stockalgorithm.train.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

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
    return ((prices().get(index) - prices().get(index - review)) /
      prices().get(index - 1)) * 100;
  }
}
