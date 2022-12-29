package de.lukasbreuer.cast.core.dataset.indicator;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;

import java.util.List;

public final class CommodityChannelIndicator extends ReviewIndicator {
  public static CommodityChannelIndicator create(List<HistoryEntry> data) {
    var indicator = new CommodityChannelIndicator(data);
    indicator.initialize();
    return indicator;
  }

  private CommodityChannelIndicator(List<HistoryEntry> data) {
    super(data);
  }

  private static final float CCI_CONSTANT = 0.015f;

  @Override
  public double calculate(int index, int review) {
    var typicalPrices = Lists.<Double>newArrayList();
    for (var i = 0; i < review; i++) {
      typicalPrices.add(calculateTypicalPrice(data().get(index - i)));
    }
    var typicalPriceAverage = calculateMovingAverage(typicalPrices, typicalPrices.size() - 1, typicalPrices.size());
    var meanDeviation = calculateMeanDeviation(typicalPrices, typicalPriceAverage);
    return ((calculateTypicalPrice(data().get(index)) - typicalPriceAverage) /
      (CCI_CONSTANT * meanDeviation)) / 100;
  }

  private double calculateMeanDeviation(List<Double> typicalPrices, double typicalPriceAverage) {
    var sum = 0D;
    for (var typicalPrice : typicalPrices) {
      sum += Math.abs(typicalPriceAverage - typicalPrice);
    }
    return sum / typicalPrices.size();
  }

  private double calculateTypicalPrice(HistoryEntry entry) {
    return (entry.high() + entry.low() + entry.close()) / 3;
  }

  private double calculateMovingAverage(
    List<Double> prices, int index, int review
  ) {
    var value = 0.0D;
    for (var i = 0; i < review; i++) {
      value += prices.get(index - i);
    }
    value /= review;
    return value;
  }
}
