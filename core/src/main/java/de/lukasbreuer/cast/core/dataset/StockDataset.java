package de.lukasbreuer.cast.core.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.dataset.indicator.IndicatorRepository;
import de.lukasbreuer.cast.core.dataset.trade.TradeGeneration;
import de.lukasbreuer.cast.core.dataset.trade.TradeTime;
import de.lukasbreuer.cast.core.neuralnetwork.HistoryIterator;
import de.lukasbreuer.cast.core.neuralnetwork.ModelState;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.Trade;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
  private final int reviewPeriod;
  private final int batchSize;
  private final int totalBatches;
  private final int tradeGeneralisationStepSize;
  private final int tradeNoiseRemovalStepSize;
  private final int inputSizePerDay;
  private final int dayLongestReview;
  private final List<Integer> optimalBuyTrades = Lists.newArrayList();
  private final List<Integer> optimalSellTrades = Lists.newArrayList();
  private List<Map.Entry<List<double[]>, Double>> dataset = Lists.newArrayList();
  private List<HistoryEntry> historyData;
  private List<DatasetDay> dayData;
  @Getter
  private HistoryIterator historyIterator;

  public void build() {
    historyData = createHistoryData();
    var indicatorRepository = IndicatorRepository.create(historyData);
    indicatorRepository.fill();
    dayData = createDayData(indicatorRepository);
    /*optimalTrades = TradeGeneration.create(historyData,
      modelState == ModelState.TRAINING ? trainMaximumTrades : evaluationMaximumTrades,
      tradeGeneralisationStepSize, tradeNoiseRemovalStepSize).determineBestTrades();*/
    if (modelState == ModelState.TRAINING) {
      /*if (symbol.name().equals("WWE")) {
        optimalTrades.add(calculateTradeFromDate(2020, 3, 16));
        optimalTrades.add(calculateTradeFromDate(2020, 3, 30));
        optimalTrades.add(calculateTradeFromDate(2020, 9, 23));
        optimalTrades.add(calculateTradeFromDate(2020, 10, 30));
      }
      //RPM
      if (symbol.name().equals("RPM")) {
        optimalTrades.add(calculateTradeFromDate(2020, 3, 23));
        optimalTrades.add(calculateTradeFromDate(2020, 6, 26));
        optimalTrades.add(calculateTradeFromDate(2020, 9, 21));
        optimalTrades.add(calculateTradeFromDate(2021, 2, 26));
        //optimalTrades.add(calculateTradeFromDate(2019, 5, 31));
      }*/
      //ES
      if (symbol.name().equals("ES")) {
        optimalBuyTrades.add(TradeTime.create(2020, 3, 23).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 4, 1).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 5, 12).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 6, 26).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 9, 21).findEntryIndex(historyData));
        //optimalBuyTrades.add(TradeTime.create(2020, 12, 23).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2021, 3, 4).findEntryIndex(historyData));

        optimalSellTrades.add(TradeTime.create(2020, 3, 30).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2020, 4, 9).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2020, 6, 3).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2020, 7, 23).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2020, 10, 8).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2021, 4, 20).findEntryIndex(historyData));
      }
      //TECH
      /*if (symbol.name().equals("TECH")) {
        optimalTrades.add(calculateTradeFromDate(2020, 3, 16));
        optimalTrades.add(calculateTradeFromDate(2020, 9, 24));
        optimalTrades.add(calculateTradeFromDate(2018, 12, 24));
      }*/
      //O
      /*optimalTrades.add(calculateTradeFromDate(2019, 5, 7));
      optimalTrades.add(calculateTradeFromDate(2018, 12, 24));
      optimalTrades.add(calculateTradeFromDate(2021, 3, 4));
      optimalTrades.add(calculateTradeFromDate(2018, 2, 8));*/
      if (symbol.name().equals("ON")) {
        //optimalBuyTrades.add(TradeTime.create(2018, 12, 24).findEntryIndex(historyData));
        //optimalBuyTrades.add(TradeTime.create(2019, 10, 8).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 3, 18).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 9, 23).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2020, 11, 2).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2021, 1, 27).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2021, 3, 8).findEntryIndex(historyData));
        optimalBuyTrades.add(TradeTime.create(2021, 5, 12).findEntryIndex(historyData));

        optimalSellTrades.add(TradeTime.create(2019, 2, 6).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2019, 5, 3).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2019, 7, 24).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2020, 1, 2).findEntryIndex(historyData));
        //optimalSellTrades.add(TradeTime.create(2020, 6, 8).findEntryIndex(historyData));
       // optimalSellTrades.add(TradeTime.create(2020, 10, 12).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2021, 1, 14).findEntryIndex(historyData));
        optimalSellTrades.add(TradeTime.create(2021, 4, 5).findEntryIndex(historyData));
      }
      //NEE
      /*if (symbol.name().equals("NEE")) {
        optimalTrades.add(calculateTradeFromDate(2020, 3, 23));
        //optimalTrades.add(calculateTradeFromDate(2020, 5, 6));
        optimalTrades.add(calculateTradeFromDate(2020, 6, 26));
        optimalTrades.add(calculateTradeFromDate(2020, 9, 23));
        //optimalTrades.add(calculateTradeFromDate(2020, 12, 4));
        //optimalTrades.add(calculateTradeFromDate(2021, 1, 4));
        optimalTrades.add(calculateTradeFromDate(2021, 3, 19));
        //optimalTrades.add(calculateTradeFromDate(2019, 11, 8));
        //optimalTrades.add(calculateTradeFromDate(2021, 9, 28));
        //optimalTrades.add(calculateTradeFromDate(2018, 12, 24));
        //optimalTrades.add(calculateTradeFromDate(2018, 2, 8));
        //optimalTrades.add(calculateTradeFromDate(2016, 11, 11));
      }*/
    }
    fillDataset();
    dataset = normalizeData(dataset);
    historyIterator = HistoryIterator.create(dataset, new Random(seed), batchSize, totalBatches);
  }

  private Trade calculateTradeFromDate(TradeTime buyTime, TradeTime sellTime) {
    return Trade.create(buyTime.findEntryIndex(historyData),
      sellTime.findEntryIndex(historyData));
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
    var bestTradeDates = tradeType.isBuy() ? optimalBuyTrades : optimalSellTrades;
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
    return dataset;
  }

  public int size() {
    return dataset.size();
  }
}