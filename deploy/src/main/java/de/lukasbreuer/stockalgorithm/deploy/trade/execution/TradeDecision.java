package de.lukasbreuer.stockalgorithm.deploy.trade.execution;

import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.deploy.trade.Trade;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor(staticName = "create")
public final class TradeDecision {
  private final TradeType tradeType;
  private final Optional<Trade> latestBuyTrade;
  private final Optional<Trade> latestSellTrade;
  private final float[] prediction;
  private final double tradePredictionMinimum;

  public boolean decide() {
    System.out.println(tradeType + ":" + Arrays.toString(prediction));
    return tradeType.isBuy() ? decideBuyTrade() : decideSellTrade();
  }

  private boolean decideBuyTrade() {
    if ((latestBuyTrade.isPresent() && latestSellTrade.isEmpty()) || (latestBuyTrade.isPresent() && latestSellTrade.isPresent() && (latestBuyTrade.get().tradeTime() > latestSellTrade.get().tradeTime()))) {
      return false;
    }
    /*if (prediction[prediction.length - 1] > 50) {
      return true;
    }*/
    return true;
  }

  private boolean decideSellTrade() {
    return false;
  }
}
