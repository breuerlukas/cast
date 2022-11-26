package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.trade.filter;

import de.lukasbreuer.stockalgorithm.core.trade.Trade;

import java.util.List;

public final class TradeIntersectionFilter extends TradeFilter {
  public static TradeIntersectionFilter create(List<Trade> initialTrades) {
    return new TradeIntersectionFilter(initialTrades);
  }

  private TradeIntersectionFilter(List<Trade> initialTrades) {
    super(initialTrades);
  }

  @Override
  public List<Trade> filter() {
    return null;
  }
}
