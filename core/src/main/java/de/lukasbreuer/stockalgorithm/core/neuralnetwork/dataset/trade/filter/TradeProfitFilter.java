package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.trade.filter;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.trade.Trade;

import java.util.*;
import java.util.stream.Collectors;

public final class TradeProfitFilter extends TradeFilter {
  public static TradeProfitFilter create(
    List<Trade> initialTrades, List<Double> prices, int maximumTrades
  ) {
    return new TradeProfitFilter(initialTrades, prices, maximumTrades);
  }

  private final List<Double> prices;
  private final int maximumTrades;

  private TradeProfitFilter(
    List<Trade> initialTrades, List<Double> prices, int maximumTrades
  ) {
    super(initialTrades);
    this.prices = prices;
    this.maximumTrades = maximumTrades;
  }

  @Override
  public List<Trade> filter() {
    return tradeProfitAllocation().stream()
      .filter(entry -> entry.getValue() > 0)
      .limit(maximumTrades)
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  private List<Map.Entry<Trade, Double>> tradeProfitAllocation() {
    var profits = Lists.<Map.Entry<Trade, Double>>newArrayList();
    for (var trade : initialTrades()) {
      profits.add(new AbstractMap.SimpleEntry(trade,
        prices.get(trade.sellTime()) - prices.get(trade.buyTime())));
    }
    Collections.sort(profits, Comparator.comparingDouble(Map.Entry::getValue));
    Collections.reverse(profits);
    return profits;
  }
}
