package de.lukasbreuer.cast.core.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.dataset.indicator.IndicatorRepository;
import de.lukasbreuer.cast.core.dataset.trade.TradeGeneration;
import de.lukasbreuer.cast.core.neuralnetwork.HistoryIterator;
import de.lukasbreuer.cast.core.neuralnetwork.ModelState;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.Trade;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class StockDataset {
  private final Symbol symbol;
  private final TradeType tradeType;
  private final ModelState modelState;
  private final int seed;
  private final int trainPeriod;
  private final int trainMaximumTrades;
  private final int evaluationPeriod;
  private final int evaluationMaximumTrades;
  private final int reviewPeriod;
  private final int batchSize;
  private final int totalBatches;
  private final int tradeGeneralisationStepSize;
  private final int tradeNoiseRemovalStepSize;
  private final int inputSizePerDay;
  private final int dayLongestReview;
  private List<Map.Entry<List<double[]>, Double>> dataset = Lists.newArrayList();
  private List<HistoryEntry> historyData;
  private List<DatasetDay> dayData;
  private List<Trade> optimalTrades;
  @Getter
  private HistoryIterator historyIterator;

  public void build() {
    historyData = createHistoryData();
    var indicatorRepository = IndicatorRepository.create(historyData);
    indicatorRepository.fill();
    dayData = createDayData(indicatorRepository);
    optimalTrades = TradeGeneration.create(historyData,
      modelState == ModelState.TRAINING ? trainMaximumTrades : evaluationMaximumTrades,
      tradeGeneralisationStepSize, tradeNoiseRemovalStepSize).determineBestTrades();
    fillDataset();
    dataset = normalizeData(dataset);
    historyIterator = HistoryIterator.create(dataset, new Random(seed), batchSize, totalBatches);
  }

  private List<HistoryEntry> createHistoryData() {
    var data = symbol.findPartOfHistory(trainPeriod + evaluationPeriod);
    if (modelState == ModelState.TRAINING) {
      data = data.stream().limit(trainPeriod).collect(Collectors.toList());
    }
    if (modelState == ModelState.EVALUATING) {
      data = data.stream().skip(trainPeriod).collect(Collectors.toList());
    }
    return data;
  }

  private List<DatasetDay> createDayData(IndicatorRepository indicatorRepository) {
    var data = Lists.<DatasetDay>newArrayList();
    for (var i = dayLongestReview; i < historyData.size(); i++) {
      var day = DatasetDay.create(i, indicatorRepository);
      day.build();
      data.add(day);
    }
    return data;
  }

  private void fillDataset() {
    var bestTradeDates = optimalTrades.stream()
      .map(trade -> tradeType.isBuy() ? trade.buyTime() : trade.sellTime())
      .collect(Collectors.toList());
    for (var i = dayLongestReview + reviewPeriod; i < historyData.size(); i++) {
      var entry = new AbstractMap.SimpleEntry<>(createInputData(i - dayLongestReview),
        calculateTradeValue(bestTradeDates, i));
      dataset.add(entry);
    }
  }

  private List<double[]> createInputData(int index) {
    var inputData = Lists.<double[]>newArrayList();
    for (var i = 0; i < reviewPeriod; i++) {
      inputData.add(dayData.get(index - i).raw());
    }
    return inputData;
  }

  private double calculateTradeValue(
    List<Integer> optimalTradeDates, int currentDate
  ) {
    for (var date : optimalTradeDates) {
      if (currentDate == date) {
        return 1;
      }
      if (currentDate == date + 1 || currentDate == date - 1) {
        return 0.67;
      }
      if (currentDate == date + 2 || currentDate == date - 2) {
        return 0.33;
      }
    }
    return 0;
  }

  private List<Map.Entry<List<double[]>, Double>> normalizeData(List<Map.Entry<List<double[]>, Double>> data) {
    var normalizedData = Lists.<Map.Entry<List<double[]>, Double>>newArrayList();
    for (var entry : data) {
      var inputData = Lists.<double[]>newArrayList();
      for (var dayInputData : entry.getKey()) {
        inputData.add(normalizeDayInputData(dayInputData));
      }
      normalizedData.add(new AbstractMap.SimpleEntry<>(inputData, entry.getValue()));
    }
    return normalizedData;
  }

  private double[] normalizeDayInputData(double[] dayInputData) {
    var dayNormalizedInput = new double[inputSizePerDay];
    for (var i = 0; i < inputSizePerDay; i++) {
      dayNormalizedInput[i] = Math.tanh(dayInputData[i]);
    }
    return dayNormalizedInput;
  }

  public List<HistoryEntry> historyData() {
    return List.copyOf(historyData);
  }

  public List<Map.Entry<List<double[]>, Double>> raw() {
    return List.copyOf(dataset);
  }

  public int size() {
    return dataset.size();
  }
}