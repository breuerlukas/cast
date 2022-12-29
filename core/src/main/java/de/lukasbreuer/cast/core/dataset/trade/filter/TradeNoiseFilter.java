package de.lukasbreuer.cast.core.dataset.trade.filter;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.trade.Trade;
import de.lukasbreuer.cast.core.trade.TradeType;

import java.util.List;
import java.util.stream.Collectors;

public final class TradeNoiseFilter extends TradeFilter {
  public static TradeNoiseFilter create(
    List<Trade> initialTrades, List<Double> prices, int noiseRemovalStepSize
  ) {
    return new TradeNoiseFilter(initialTrades, prices, noiseRemovalStepSize);
  }

  private final List<Double> prices;
  private final int noiseRemovalStepSize;

  private TradeNoiseFilter(
    List<Trade> initialTrades, List<Double> prices, int noiseRemovalStepSize
  ) {
    super(initialTrades);
    this.prices = prices;
    this.noiseRemovalStepSize = noiseRemovalStepSize;
  }

  @Override
  public List<Trade> filter() {
    var result = Lists.<Trade>newArrayList();
    for (var trade : initialTrades()) {
      result.add(Trade.create(calculateNoiselessSignal(trade, prices, TradeType.BUY),
        calculateNoiselessSignal(trade, prices, TradeType.SELL)));
    }
    return result.stream()
      .filter(trade -> trade.sellTime() > trade.buyTime())
      .collect(Collectors.toList());
  }

  private int calculateNoiselessSignal(
    Trade trade, List<Double> closeData, TradeType type
  ) {
    var updatedTrade = trade;
    for (var i = 0; i < (noiseRemovalStepSize - 1) / 2; i++) {
      var forwardOptimal = calculateDirectionalNoiselessSignal(updatedTrade,
        closeData, type, noiseRemovalStepSize - 2 * i, + 1);
      var backwardOptimal = calculateDirectionalNoiselessSignal(updatedTrade,
        closeData, type, noiseRemovalStepSize - 2 * i, - 1);
      var noiselessSignal = determineOptimalSignal(updatedTrade, closeData, type, forwardOptimal, backwardOptimal);
      updatedTrade = Trade.create(type.isBuy() ? noiselessSignal : updatedTrade.buyTime(),
        type.isSell() ? noiselessSignal : updatedTrade.sellTime());
    }
    return type.isBuy() ? updatedTrade.buyTime() : updatedTrade.sellTime();
  }

  private int determineOptimalSignal(
    Trade trade, List<Double> closeData, TradeType type,
    int forwardOptimal, int backwardOptimal
  ) {
    var forwardPrice = closeData.get(forwardOptimal);
    var backwardPrice = closeData.get(backwardOptimal);
    var tradeBuyPrice = closeData.get(trade.buyTime());
    if (type.isBuy() && tradeBuyPrice < forwardPrice && tradeBuyPrice < backwardPrice) {
      return trade.buyTime();
    }
    var tradeSellPrice = closeData.get(trade.sellTime());
    if (type.isSell() && tradeSellPrice > forwardPrice && tradeSellPrice > backwardPrice) {
      return trade.sellTime();
    }
    if (type.isBuy() && forwardPrice < backwardPrice || type.isSell() && forwardPrice > backwardPrice) {
      return forwardOptimal;
    }
    return backwardOptimal;
  }

  private int calculateDirectionalNoiselessSignal(
    Trade trade, List<Double> closeData, TradeType type,
    int stepSize, int direction
  ) {
    var initialSignal = type.isBuy() ? trade.buyTime() : trade.sellTime();
    var lastAverage = calculateMovingAverage(closeData, initialSignal - ((stepSize - 1) / 2) - direction, stepSize);
    var calculationLength = direction > 0 ? Math.min(closeData.size() - initialSignal - stepSize, 30) :
      Math.min(initialSignal - 30 - stepSize, 30);
    for (var i = 0; i < calculationLength; i++) {
      var average = calculateMovingAverage(closeData, initialSignal - ((stepSize - 1) / 2) + i * direction, stepSize);
      if (type.isBuy() ? average > lastAverage : average < lastAverage) {
        return initialSignal + i * direction;
      }
      lastAverage = average;
    }
    return initialSignal;
  }

  private double calculateMovingAverage(
    List<Double> prices, int skipDays, int period
  ) {
    var value = 0.0;
    for (var i = skipDays; i < (period + skipDays); i++) {
      if (i < 0 || i >= prices.size()) {
        continue;
      }
      value += prices.get(i);
    }
    value /= period;
    return value;
  }
}
