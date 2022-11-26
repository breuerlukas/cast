package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.trade.filter;

import de.lukasbreuer.stockalgorithm.core.trade.Trade;

import java.util.List;

public final class TradeNoiseFilter extends TradeFilter {
  public static TradeNoiseFilter create(
    List<Trade> initialTrades, List<Double> prices
  ) {
    return new TradeNoiseFilter(initialTrades, prices);
  }

  private final List<Double> prices;

  private TradeNoiseFilter(List<Trade> initialTrades, List<Double> prices) {
    super(initialTrades);
    this.prices = prices;
  }

  @Override
  public List<Trade> filter() {
    return null;
  }
}
