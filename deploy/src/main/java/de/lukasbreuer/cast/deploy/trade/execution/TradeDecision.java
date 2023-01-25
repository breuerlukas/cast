package de.lukasbreuer.cast.deploy.trade.execution;

import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.core.trade.TradeType;
import de.lukasbreuer.cast.deploy.portfolio.Stock;
import de.lukasbreuer.cast.deploy.trade.Trade;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor(staticName = "create")
public final class TradeDecision {
  private final Log log;
  private final Stock stock;
  private final TradeType tradeType;
  private final Optional<Trade> latestBuyTrade;
  private final Optional<Trade> latestSellTrade;
  private final float[] prediction;
  private final double tradePredictionMinimum;

  public boolean decide() {
    return tradeType.isBuy() ? decideBuyTrade() : decideSellTrade();
  }

  private boolean decideBuyTrade() {
    if ((latestBuyTrade.isPresent() && latestSellTrade.isEmpty()) ||
      (latestBuyTrade.isPresent() && latestSellTrade.isPresent() &&
        (latestBuyTrade.get().tradeTime() > latestSellTrade.get().tradeTime()))
    ) {
      return false;
    }
    return prediction[prediction.length - 2] > tradePredictionMinimum &&
      prediction[prediction.length - 1] < prediction[prediction.length - 2];
  }

  private boolean decideSellTrade() {
    if (latestBuyTrade.isEmpty() ||
      (latestBuyTrade.isPresent() && latestSellTrade.isPresent() &&
        (latestSellTrade.get().tradeTime() > latestBuyTrade.get().tradeTime()))) {
      return false;
    }
    return prediction[prediction.length - 2] > tradePredictionMinimum &&
      prediction[prediction.length - 1] < prediction[prediction.length - 2];
  }
}
