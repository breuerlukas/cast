package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator;

import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;

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
    return 100 * ((prices().get(index) - prices().get(index - review)) /
      prices().get(index - review));
  }
}
