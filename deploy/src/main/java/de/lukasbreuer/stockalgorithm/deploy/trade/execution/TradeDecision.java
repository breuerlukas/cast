package de.lukasbreuer.stockalgorithm.deploy.trade.execution;

import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.Stock;
import de.lukasbreuer.stockalgorithm.deploy.trade.Trade;
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
    log.info("A decision is currently being made on the purchase of a " +
      stock.formattedStockName() + " stock");
    log.info("These are the last 5 daily predictions " +
      Arrays.toString(Arrays.copyOfRange(prediction, prediction.length - 5, prediction.length)));
    if (prediction[prediction.length - 1] > tradePredictionMinimum) {
      return true;
    }
    return false;
  }

  private boolean decideSellTrade() {
    if (latestBuyTrade.isEmpty() ||
      (latestBuyTrade.isPresent() && latestSellTrade.isPresent() &&
        (latestSellTrade.get().tradeTime() > latestBuyTrade.get().tradeTime()))) {
      return false;
    }
    log.info("A decision is currently being made on the sale of a " +
      stock.formattedStockName() + " stock");
    log.info("These are the last 5 daily predictions " +
      Arrays.toString(Arrays.copyOfRange(prediction, prediction.length - 5, prediction.length)));
    if (prediction[prediction.length - 1] > tradePredictionMinimum) {
      return true;
    }
    return false;
  }
}
