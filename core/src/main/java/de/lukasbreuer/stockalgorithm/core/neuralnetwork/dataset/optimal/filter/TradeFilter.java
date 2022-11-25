package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.optimal.filter;

import de.lukasbreuer.stockalgorithm.core.trade.Trade;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TradeFilter {
  private final List<Trade> initialTrades;

  public abstract List<Trade> filter();
}
