package de.lukasbreuer.stockalgorithm;

import com.github.sh0nk.matplotlib4j.Plot;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

final class TradeTimeAlgorithmTest {
  @Test
  void testTradeTiming() throws Exception {
    var symbol = Symbol.createAndFetch("AMZN");
    var days = 365 * 2;
    var stepSize = 7;
    var history = symbol.findPartOfHistory(days);
    var closeData = history.stream().map(HistoryEntry::close).collect(Collectors.toList());
    var data = Lists.<Map.Entry<Integer, Double>>newArrayList();
    for (var i = 1; i < days / stepSize; i++) {
      var entryIndex = i * stepSize;
      data.add(new AbstractMap.SimpleEntry<>(calculateDayFromStep(i, stepSize), calculateMovingAverage(closeData, entryIndex, stepSize)));
    }
    System.out.println("DATA SIZE: " + data.size());

    int maxTrades = 5;
    var prices = data.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    var allTrades = findAllPossibleTrades(prices);
    var valuableTrades = filterMostValuableTrades(prices, allTrades, maxTrades);
    for (var trade : valuableTrades) {
      System.out.print("Trade: ");
      System.out.print("Buy=" + calculateDayFromStep(trade.buyTime() + 1, stepSize) + "; ");
      System.out.println("Sell=" + calculateDayFromStep(trade.sellTime() + 1, stepSize) + " ");
    }

    Plot plt = Plot.create();
    plt.plot()
      .add(data.stream().map(Map.Entry::getKey).collect(Collectors.toList()), data.stream().map(Map.Entry::getValue).collect(Collectors.toList()))
      .add(closeData)
      .linestyle("-");
    plt.xlabel("Time");
    plt.ylabel("Value");
    plt.title("Stock Data");
    plt.show();
  }

  List<Trade> filterMostValuableTrades(List<Double> prices, List<Trade> trades, int maxTrades) {
    var profits = Lists.<Map.Entry<Trade, Double>>newArrayList();
    for (var trade : trades) {
      profits.add(new AbstractMap.SimpleEntry(trade,
        prices.get(trade.sellTime()) - prices.get(trade.buyTime())));
    }
    Collections.sort(profits, Comparator.comparingDouble(Map.Entry::getValue));
    Collections.reverse(profits);
    return profits.stream()
      .filter(entry -> entry.getValue() > 0)
      .limit(maxTrades)
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  List<Trade> findAllPossibleTrades(List<Double> prices) {
    if (prices.size() < 2) {
      return Lists.newArrayList();
    }
    var trades = Lists.<Trade>newArrayList();
    int sellingTime = 0;
    int buyingTime = 0;
    for (int i = 1; i < prices.size(); i++) {
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

  double calculateMovingAverage(List<Double> prices, int skipDays, int period) {
    double value = 0.0;
    for (int i = skipDays; i < (period + skipDays); i++) {
      value += prices.get(i);
    }
    value /= period;
    return value;
  }

  int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
  }
}
