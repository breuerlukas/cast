package de.lukasbreuer.cast.core.dataset.indicator;

import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class BalanceVolumeIndicator extends ReviewIndicator {
  public static BalanceVolumeIndicator create(List<HistoryEntry> data) {
    var indicator = new BalanceVolumeIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private BalanceVolumeIndicator(List<HistoryEntry> data) {
    super(data);
  }

  @Override
  public double calculate(int index, int review) {
    double obv = 0;
    for (var i = 0; i < review; i++) {
      var currentVolume = data().get(index - i).volume();
      var previousVolume = data().get(index - i - 1).volume();
      if (currentVolume > previousVolume) {
        obv += currentVolume;
      }
      if (currentVolume < previousVolume) {
        obv -= currentVolume;
      }
    }
    return obv / 1e+9;
  }
}