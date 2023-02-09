package de.lukasbreuer.cast.train.evaluation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public final class Evaluation {
  private final TradeType tradeType;
  private final NeuralNetwork neuralNetwork;
  private final StockDataset evaluationDataset;
  private final int buyReviewPeriod;
  private final int sellReviewPeriod;
  private final int dayLongestReview;
  private final int evaluationMaximumTrades;
  private Map<Integer, Float> timePredictionAllocation;
  private List<Integer> optimalSignals;
  private List<Integer> determinedSignals;

  public void analyse() {
    timePredictionAllocation = createTimePredictionAllocation();
    optimalSignals = findOptimalSignals();
    determinedSignals = selectPromisingSignals(timePredictionAllocation);
  }

  private List<Integer> selectPromisingSignals(
    Map<Integer, Float> timePredictionAllocation
  ) {
    return timePredictionAllocation.entrySet().stream()
      .sorted(Map.Entry.<Integer, Float>comparingByValue().reversed())
      .map(Map.Entry::getKey)
      .limit(evaluationMaximumTrades)
      .collect(Collectors.toList());
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

  public Map<Integer, Float> timePredictionAllocation() {
    return Maps.newHashMap(timePredictionAllocation);
  }

  public List<Integer> optimalSignals() {
    return List.copyOf(optimalSignals);
  }

  public List<Integer> determinedSignals() {
    return List.copyOf(determinedSignals);
  }
}
