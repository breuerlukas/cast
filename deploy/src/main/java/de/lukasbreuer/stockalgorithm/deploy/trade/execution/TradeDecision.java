package de.lukasbreuer.stockalgorithm.deploy.trade.execution;

import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.Stock;
import de.lukasbreuer.stockalgorithm.deploy.trade.Trade;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(staticName = "create")
public final class TradeDecision {
  private final Stock stock;
  private final TradeType tradeType;
  private final Optional<Trade> latestTrade;
  private final float[] prediction;

  //TODO: IMPLEMENT
  public boolean decide() {
    return false;
  }
}
