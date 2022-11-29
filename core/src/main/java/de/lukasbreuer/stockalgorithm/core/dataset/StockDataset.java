package de.lukasbreuer.stockalgorithm.core.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.dataset.trade.TradeGeneration;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.ModelState;
import de.lukasbreuer.stockalgorithm.core.dataset.indicator.IndicatorRepository;
import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import de.lukasbreuer.stockalgorithm.core.symbol.Symbol;
import de.lukasbreuer.stockalgorithm.core.trade.Trade;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
  private final List<Map.Entry<List<double[]>, Double>> dataset = Lists.newArrayList();
  private List<HistoryEntry> historyData;
  private List<DatasetDay> dayData;
  private List<Trade> optimalTrades;
  private HistoryIterator historyIterator;

  public void build() {
    historyData = createHistoryData();
    var indicatorRepository = IndicatorRepository.create(historyData);
    dayData = createDayData(indicatorRepository);
    optimalTrades = TradeGeneration.create(historyData,
      modelState == ModelState.TRAINING ? trainMaximumTrades : evaluationMaximumTrades,
      tradeGeneralisationStepSize, tradeNoiseRemovalStepSize).determineBestTrades();
    fillDataset();
    historyIterator = HistoryIterator.create(dataset, new Random(seed), batchSize, totalBatches);
  }

  private List<HistoryEntry> createHistoryData() {
    var data = symbol.findPartOfHistory(trainPeriod + evaluationPeriod);
    if (modelState == ModelState.TRAINING) {
      data = data.stream().limit(trainPeriod).collect(Collectors.toList());
    }
    if (modelState == ModelState.EVALUATING) {
      data = data.stream().skip(evaluationPeriod).collect(Collectors.toList());
    }
    return data;
  }

  private List<DatasetDay> createDayData(IndicatorRepository indicatorRepository) {
    var data = Lists.<DatasetDay>newArrayList();
    for (var i = 0; i < historyData.size(); i++) {
      var day = DatasetDay.create(i, indicatorRepository);
      day.build();
      data.add(day);
    }
    return data;
  }

  private static final int DATASET_DAY_LONGEST_REVIEW = 21;

  private void fillDataset() {
    var bestTradeDates = optimalTrades.stream()
      .map(trade -> tradeType == TradeType.BUY ? trade.buyTime() : trade.sellTime())
      .collect(Collectors.toList());
    for (var i = DATASET_DAY_LONGEST_REVIEW + reviewPeriod; i < historyData.size(); i++) {
      var entry = new AbstractMap.SimpleEntry<>(createInputData(i),
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

  public HistoryIterator historyIterator() {
    return historyIterator;
  }

  public List<Map.Entry<List<double[]>, Double>> raw() {
    return List.copyOf(dataset);
  }
}
