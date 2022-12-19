package de.lukasbreuer.stockalgorithm.train.dataset.trade;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import de.lukasbreuer.stockalgorithm.core.trade.Trade;
import de.lukasbreuer.stockalgorithm.train.dataset.trade.filter.TradeIntersectionFilter;
import de.lukasbreuer.stockalgorithm.train.dataset.trade.filter.TradeNoiseFilter;
import de.lukasbreuer.stockalgorithm.train.dataset.trade.filter.TradeProfitFilter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public class TradeGeneration {
  private final List<HistoryEntry> data;
  private final int maximumTrades;
  private final int generalisationStepSize;
  private final int noiseRemovalStepSize;

  public List<Trade> determineBestTrades() {
    var prices = data.stream().map(HistoryEntry::close).collect(Collectors.toList());
    var allTrades = findAllPossibleTrades(prices);
    var noiselessTrades = TradeNoiseFilter.create(allTrades, prices, noiseRemovalStepSize).filter();
    var collapsedTrades = TradeIntersectionFilter.create(noiselessTrades).filter();
    return TradeProfitFilter.create(collapsedTrades, prices, maximumTrades).filter();
  }

  private List<Trade> findAllPossibleTrades(List<Double> prices) {
    var data = Lists.<Double>newArrayList();
    for (var i = 1; i < prices.size() / generalisationStepSize; i++) {
      var entryIndex = i * generalisationStepSize;
      data.add(calculateMovingAverage(prices, entryIndex, generalisationStepSize));
    }
    return createGeneralizedTrades(data).stream()
      .map(trade -> Trade.create(calculateDayFromStep(trade.buyTime() + 1, generalisationStepSize),
        calculateDayFromStep(trade.sellTime() + 1, generalisationStepSize)))
      .collect(Collectors.toList());
  }

  private List<Trade> createGeneralizedTrades(List<Double> prices) {
    var trades = Lists.<Trade>newArrayList();
    var buyingTime = 0;
    var sellingTime = 0;
    for (var i = 1; i < prices.size(); i++) {
      if (prices.get(i) >= prices.get(i - 1)) {
        sellingTime++;
      } else {
        trades.add(Trade.create(buyingTime, sellingTime));
        sellingTime = buyingTime = i;
      }
    }
    trades.add(Trade.create(buyingTime, sellingTime));
    return trades;
  }

  private int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
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
