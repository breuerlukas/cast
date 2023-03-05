package de.lukasbreuer.cast.core.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.dataset.indicator.IndicatorRepository;
import de.lukasbreuer.cast.core.neuralnetwork.HistoryIterator;
import de.lukasbreuer.cast.core.neuralnetwork.ModelState;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.TradeTime;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
  private final int buyReviewPeriod;
  private final int sellReviewPeriod;
  private final int batchSize;
  private final int totalBatches;
  private final int tradeGeneralisationStepSize;
  private final int tradeNoiseRemovalStepSize;
  private final int inputSizePerDay;
  private final int dayLongestReview;
  private List<Map.Entry<List<double[]>, Double>> dataset = Lists.newArrayList();
  private List<HistoryEntry> historyData;
  private List<DatasetDay> dayData;
  private DatasetConfiguration datasetConfiguration;
  @Getter
  private HistoryIterator historyIterator;

  public void build() {
    historyData = createHistoryData();
    var indicatorRepository = IndicatorRepository.create(historyData);
    indicatorRepository.fill();
    dayData = createDayData(indicatorRepository);
    if (modelState == ModelState.TRAINING) {
      try {
        datasetConfiguration = DatasetConfiguration.createAndLoad(symbol.name());
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
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
    for (var i = dayLongestReview + reviewPeriod(); i < historyData.size(); i++) {
      var entry = new AbstractMap.SimpleEntry<>(createInputData(i - dayLongestReview),
        calculateTradeValue(tradeTimes().stream().map(bestTradeDate ->
          bestTradeDate.findEntryIndex(historyData)).collect(Collectors.toList()), i));
      dataset.add(entry);
    }
  }

  private List<double[]> createInputData(int index) {
    var inputData = Lists.<double[]>newArrayList();
    for (var i = 0; i < reviewPeriod(); i++) {
      inputData.add(dayData.get(index - i).raw());
    }
    return inputData;
  }

  private static final int TRADE_VALUE_DISTRIBUTION = 1;

  private double calculateTradeValue(
    List<Integer> optimalTradeDates, int currentDate
  ) {
    for (var date : optimalTradeDates) {
      for (var i = 0; i < TRADE_VALUE_DISTRIBUTION; i++) {
        if (currentDate == date - i || currentDate == date + i) {
          return (TRADE_VALUE_DISTRIBUTION - i) / ((double) TRADE_VALUE_DISTRIBUTION);
        }
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

  private List<TradeTime> tradeTimes() {
    if (datasetConfiguration == null) {
      return Lists.newArrayList();
    }
    return tradeType.isBuy() ? datasetConfiguration.buyTradeTimes() :
      datasetConfiguration.sellTradeTimes();
  }

  private int reviewPeriod() {
    return tradeType.isBuy() ? buyReviewPeriod : sellReviewPeriod;
  }

  public List<HistoryEntry> historyData() {
    return List.copyOf(historyData);
  }

  public List<Map.Entry<List<double[]>, Double>> raw() {
    return dataset;
  }

  public int size() {
    return dataset.size();
  }
}