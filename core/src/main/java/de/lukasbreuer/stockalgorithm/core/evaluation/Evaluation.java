package de.lukasbreuer.stockalgorithm.core.evaluation;

import com.google.common.collect.Maps;
import de.lukasbreuer.stockalgorithm.core.dataset.StockDataset;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetwork;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public final class Evaluation {
  private final NeuralNetwork neuralNetwork;
  private final StockDataset evaluationDataset;
  private final int reviewPeriod;
  private final int dayLongestReview;
  private final int evaluationMaximumTrades;
  private List<Integer> optimalSignals;
  private List<Integer> determinedSignals;

  public void analyse() {
    var timePredictionAllocation = timePredictionAllocation();
    optimalSignals = timePredictionAllocation.entrySet().stream()
      .filter(entry -> entry.getValue() == 1)
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
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

  private Map<Integer, Float> timePredictionAllocation() {
    var allocation = Maps.<Integer, Float>newHashMap();
    var rawDataset = evaluationDataset.raw();
    for (var i = 1; i < evaluationDataset.size() - 1; i++) {
      var entryTime = i + dayLongestReview + reviewPeriod;
      allocation.put(entryTime, neuralNetwork.evaluate(entryTime, rawDataset.get(i)));
    }
    return allocation;
  }

  public List<Integer> optimalSignals() {
    return List.copyOf(optimalSignals);
  }

  public List<Integer> determinedSignals() {
    return List.copyOf(determinedSignals);
  }
}
