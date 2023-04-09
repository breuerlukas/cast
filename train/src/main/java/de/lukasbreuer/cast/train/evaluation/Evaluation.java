package de.lukasbreuer.cast.train.evaluation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Evaluation {
  private enum ExtremePriceType {
    HIGHEST,
    LOWEST;

    public boolean isHighest() {
      return this == HIGHEST;
    }

    public boolean isLowest() {
      return this == LOWEST;
    }
  }

  private final TradeType tradeType;
  private final NeuralNetwork neuralNetwork;
  private final StockDataset evaluationDataset;
  private final int buyReviewPeriod;
  private final int sellReviewPeriod;
  private final int dayLongestReview;
  private Map<Integer, Float> timePredictionAllocation;
  private List<Integer> optimalSignals;
  @Getter
  private double highestPrice;
  @Getter
  private double lowestPrice;

  public void analyse() {
    timePredictionAllocation = createTimePredictionAllocation();
    optimalSignals = findOptimalSignals();
    highestPrice = findExtremePrice(ExtremePriceType.HIGHEST);
    lowestPrice = findExtremePrice(ExtremePriceType.LOWEST);
  }

  private List<Integer> findOptimalSignals() {
    var result = Lists.<Integer>newArrayList();
    var rawDataset = evaluationDataset.raw();
    for (var i = 1; i < evaluationDataset.size() - 1; i++) {
      var entryTime = i + dayLongestReview + (tradeType.isBuy() ?
        buyReviewPeriod : sellReviewPeriod);
      if (rawDataset.get(i).getValue() == 1) {
        result.add(entryTime);
      }
    }
    return result;
  }

  private Map<Integer, Float> createTimePredictionAllocation() {
    var allocation = Maps.<Integer, Float>newHashMap();
    var rawDataset = evaluationDataset.raw();
    for (var i = 1; i < evaluationDataset.size(); i++) {
      var entryTime = i + dayLongestReview + (tradeType.isBuy() ?
        buyReviewPeriod : sellReviewPeriod);
      allocation.put(entryTime, neuralNetwork.evaluate(entryTime, rawDataset.get(i), true));
    }
    return allocation;
  }

  private double findExtremePrice(ExtremePriceType type) {
    double currentExtremePrice = type.isHighest() ? 0 : Double.MAX_VALUE;
    for (var historyEntry : evaluationDataset.historyData()) {
      var entryPrice = historyEntry.close();
      if (isExtremePrice(type, entryPrice, currentExtremePrice)) {
        currentExtremePrice = entryPrice;
      }
    }
    return currentExtremePrice;
  }

  private boolean isExtremePrice(
    ExtremePriceType type, double price, double currentExtremePrice
  ) {
    if (type.isHighest() && price > currentExtremePrice) {
      return true;
    }
    if (type.isLowest() && price < currentExtremePrice) {
      return true;
    }
    return false;
  }

  public double priceSpan() {
    return highestPrice - lowestPrice;
  }

  public Map<Integer, Float> timePredictionAllocation() {
    return Maps.newHashMap(timePredictionAllocation);
  }

  public List<Integer> optimalSignals() {
    return List.copyOf(optimalSignals);
  }
}
