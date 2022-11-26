package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.trade;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import de.lukasbreuer.stockalgorithm.core.trade.Trade;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

//TODO: REWRITE CODE TO ACHIEVE GREATER CLARITY
@RequiredArgsConstructor(staticName = "create")
public class TradeGeneration {
  private final List<HistoryEntry> data;
  private final int maximumTrades;
  private final int generalisationStepSize;
  private final int noiseRemovalStepSize;

  private static final int GENERALISATION_STEP_SIZE = 15;

  public List<Trade> findBestTrades(List<HistoryEntry> history, int maxTrades) {
    var closeData = history.stream().map(HistoryEntry::close).collect(Collectors.toList());
    var data = Lists.<Double>newArrayList();
    for (var i = 1; i < history.size() / GENERALISATION_STEP_SIZE; i++) {
      var entryIndex = i * GENERALISATION_STEP_SIZE;
      data.add(calculateMovingAverage(closeData, entryIndex, GENERALISATION_STEP_SIZE));
    }
    var allTrades = findAllPossibleTrades(data);
    var noiselessTrades = filterNoiseOutOfTrades(allTrades, closeData);
    var collapsedTrades = collapseIntersectingTrades(noiselessTrades);
    return filterMostValuableTrades(closeData, collapsedTrades, maxTrades);
  }

  private List<Trade> collapseIntersectingTrades(List<Trade> trades) {
    var removed = Lists.<Trade>newArrayList();
    var result = Lists.<Trade>newArrayList();
    for (var i = 0; i < trades.size(); i++) {
      var trade = trades.get(i);
      if (removed.contains(trade)) {
        continue;
      }
      result.add(trade);
      for (var j = 0; j < trades.size(); j++) {
        var reviewedTrade = trades.get(j);
        if (reviewedTrade.equals(trade) || removed.contains(reviewedTrade)) {
          continue;
        }
        if (reviewedTrade.buyTime() >= trade.buyTime() && reviewedTrade.sellTime() <= trade.sellTime()) {
          removed.add(reviewedTrade);
          result.remove(reviewedTrade);
          continue;
        }
        if (reviewedTrade.buyTime() >= trade.buyTime() && reviewedTrade.buyTime() <= trade.sellTime() && reviewedTrade.sellTime() >= trade.sellTime()) {
          removed.add(reviewedTrade);
          result.remove(reviewedTrade);
          trade.sellTime(reviewedTrade.sellTime());
          continue;
        }
        if (reviewedTrade.sellTime() >= trade.buyTime() && reviewedTrade.sellTime() <= trade.sellTime() && reviewedTrade.buyTime() <= trade.buyTime()) {
          removed.add(reviewedTrade);
          result.remove(reviewedTrade);
          trade.buyTime(reviewedTrade.buyTime());
        }
      }
    }
    return result;
  }

  private List<Trade> filterNoiseOutOfTrades(List<Trade> trades, List<Double> closeData) {
    var result = Lists.<Trade>newArrayList();
    for (var trade : trades) {
      result.add(Trade.create(calculateNoiselessSignal(trade, closeData, TradeType.BUY),
        calculateNoiselessSignal(trade, closeData, TradeType.SELL)));
    }
    return result.stream()
      .filter(trade -> trade.sellTime() > trade.buyTime())
      .collect(Collectors.toList());
  }

  private static final int NOISE_REMOVAL_STEP_SIZE = GENERALISATION_STEP_SIZE;

  private int calculateNoiselessSignal(Trade trade, List<Double> closeData, TradeType type) {
    var updatedTrade = trade;
    for (var i = 0; i < (NOISE_REMOVAL_STEP_SIZE - 1) / 2; i++) {
      var forwardOptimal = calculateDirectionalNoiselessSignal(updatedTrade,
        closeData, type, NOISE_REMOVAL_STEP_SIZE - 2 * i, + 1);
      var backwardOptimal = calculateDirectionalNoiselessSignal(updatedTrade,
        closeData, type, NOISE_REMOVAL_STEP_SIZE - 2 * i, - 1);
      var noiselessSignal = determineOptimalSignal(updatedTrade, closeData, type, forwardOptimal, backwardOptimal);
      updatedTrade = Trade.create(type == TradeType.BUY ? noiselessSignal : updatedTrade.buyTime(),
        type == TradeType.SELL ? noiselessSignal : updatedTrade.sellTime());
    }
    return type == TradeType.BUY ? updatedTrade.buyTime() : updatedTrade.sellTime();
  }

  private int determineOptimalSignal(
    Trade trade, List<Double> closeData, TradeType type,
    int forwardOptimal, int backwardOptimal
  ) {
    var forwardPrice = closeData.get(forwardOptimal);
    var backwardPrice = closeData.get(backwardOptimal);
    var tradeBuyPrice = closeData.get(trade.buyTime());
    if (type == TradeType.BUY && tradeBuyPrice < forwardPrice && tradeBuyPrice < backwardPrice) {
      return trade.buyTime();
    }
    var tradeSellPrice = closeData.get(trade.sellTime());
    if (type == TradeType.SELL && tradeSellPrice > forwardPrice && tradeSellPrice > backwardPrice) {
      return trade.sellTime();
    }
    if (type == TradeType.BUY && forwardPrice < backwardPrice || type == TradeType.SELL && forwardPrice > backwardPrice) {
      return forwardOptimal;
    }
    return backwardOptimal;
  }

  private int calculateDirectionalNoiselessSignal(Trade trade, List<Double> closeData, TradeType type, int stepSize, int direction) {
    var initialSignal = type == TradeType.BUY ? trade.buyTime() : trade.sellTime();
    var lastAverage = calculateMovingAverage(closeData, initialSignal - ((stepSize - 1) / 2) - direction, stepSize);
    var calculationLength = direction > 0 ? Math.min(closeData.size() - initialSignal - stepSize, 30) :
      Math.min(initialSignal - 30 - stepSize, 30);
    for (var i = 0; i < calculationLength; i++) {
      var average = calculateMovingAverage(closeData, initialSignal - ((stepSize - 1) / 2) + i * direction, stepSize);
      if (type == TradeType.BUY ? average > lastAverage : average < lastAverage) {
        return initialSignal + i * direction;
      }
      lastAverage = average;
    }
    return initialSignal;
  }

  private List<Trade> filterMostValuableTrades(
    List<Double> prices, List<Trade> trades, int maxTrades
  ) {
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

  private List<Trade> findAllPossibleTrades(List<Double> prices) {
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
    return trades.stream()
      .map(trade -> Trade.create(calculateDayFromStep(trade.buyTime() + 1, GENERALISATION_STEP_SIZE),
        calculateDayFromStep(trade.sellTime() + 1, GENERALISATION_STEP_SIZE)))
      .collect(Collectors.toList());
  }

  private int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
  }

  private double calculateMovingAverage(List<Double> prices, int skipDays, int period) {
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
