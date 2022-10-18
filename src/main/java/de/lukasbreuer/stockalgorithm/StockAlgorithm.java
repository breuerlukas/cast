package de.lukasbreuer.stockalgorithm;

import ai.djl.engine.Engine;
import com.clearspring.analytics.util.Lists;
import com.github.sh0nk.matplotlib4j.Plot;
import de.lukasbreuer.stockalgorithm.djl.NeuralNetwork;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

public final class StockAlgorithm {
  private static final int SEED = 12345;
  private static final float LEARNING_RATE = 0.05f;
  private static final float DROPOUT_RATE = 1f;
  private static final int ITERATIONS = 1;
  private static final int EPOCHS = 30;
  private static final int HIDDEN_NEURONS = 1024;
  private static final int BATCH_SIZE = 10;
  private static final int TOTAL_BATCHES = 500;
  private static final int INPUT_SIZE_PER_DAY = 29; //29
  private static final int DAY_REVIEW = 1;
  private static final int TRAIN_DAYS = 365 * 8;
  private static final int TRAIN_MAX_TRADES = 16;
  private static final int EVALUATION_DAYS = 365 * 2;
  private static final int EVALUATION_MAX_TRADES = 4;
  private static final int GENERALISATION_STEP_SIZE = 7;
  private static final String STOCK = "AMZN";

  public static void main(String[] args) throws Exception {
    Engine.getInstance().setRandomSeed(SEED);
    var symbol = Symbol.createAndFetch(STOCK);
    System.out.println(Arrays.toString(createSingleInputData(symbol.findPartOfHistory(365),
      symbol.findPartOfHistory(365).stream().mapToDouble(HistoryEntry::close).max().getAsDouble(), 21, 0)));
    for (var trade : findBestTrades(symbol.findPartOfHistory(TRAIN_DAYS + EVALUATION_DAYS).stream().skip(TRAIN_DAYS).collect(Collectors.toList()), EVALUATION_MAX_TRADES)) {
      System.out.println(calculateDayFromStep(trade.buyTime() + 1, GENERALISATION_STEP_SIZE) + ":" + calculateDayFromStep(trade.sellTime() + 1, GENERALISATION_STEP_SIZE));
    }
    var buyNetwork = buildBuyNetwork(symbol);
    var sellNetwork = buildSellNetwork(symbol);
    System.out.println("TRAINING");
    System.out.println("");
    System.out.println("TRAIN BUY NETWORK");
    var buyEvaluationDataset = createDataset(symbol, TradeType.BUY, ModelState.EVALUATING);
    buyNetwork.train(EPOCHS, buyEvaluationDataset.stream().filter(entry -> entry.getValue() == 1).findFirst().get());
    System.out.println("EVALUATE BUY NETWORK");
    displayGraph(symbol, buyNetwork, buyEvaluationDataset, 0.8f, 200, "BUY");
    System.out.println("TRAIN SELL NETWORK");
    var sellEvaluationDataset = createDataset(symbol, TradeType.SELL, ModelState.EVALUATING);
    sellNetwork.train(EPOCHS, sellEvaluationDataset.stream().filter(entry -> entry.getValue() == 1).findFirst().get());
    System.out.println("EVALUATE SELL NETWORK");
    displayGraph(symbol, sellNetwork, sellEvaluationDataset, 0.8f, 200, "SELL");
  }

  private static void displayGraph(Symbol symbol, NeuralNetwork network, List<Map.Entry<List<double[]>, Double>> dataset, float minPrediction, int scale, String title) throws Exception {
    Plot plot = Plot.create();

    for (var i = 0; i < dataset.size() - 1; i++) {
      var drawIndex = i + 21 + DAY_REVIEW;
      if (network.evaluate(drawIndex, dataset.get(i)) > minPrediction && (network.evaluate(drawIndex - 1, dataset.get(i - 1)) > 0.2 || network.evaluate(drawIndex + 1, dataset.get(i + 1)) > 0.2)) {
        plot.plot().add(List.of(drawIndex, drawIndex), List.of(0, scale));
        System.out.println("Add Line " + i + " To Graph");
      }
      if (dataset.get(i).getValue() == 1) {
        plot.plot().add(List.of(drawIndex, drawIndex), List.of(scale / 2, scale + (scale / 4)));
      }
    }

    var closeData = symbol.findPartOfHistory(TRAIN_DAYS + EVALUATION_DAYS).stream().skip(TRAIN_DAYS).map(HistoryEntry::close).collect(Collectors.toList());
    var averageData = Lists.<Map.Entry<Integer, Double>>newArrayList();
    for (var i = 1; i < EVALUATION_DAYS / GENERALISATION_STEP_SIZE; i++) {
      var entryIndex = i * GENERALISATION_STEP_SIZE;
      averageData.add(new AbstractMap.SimpleEntry<>(calculateDayFromStep(i, GENERALISATION_STEP_SIZE), calculateSMA(closeData, entryIndex, GENERALISATION_STEP_SIZE)));
    }

    new Thread(new Runnable() {
      @SneakyThrows
      @Override
      public void run() {
        plot.plot()
          .add(averageData.stream().map(Map.Entry::getKey).collect(Collectors.toList()), averageData.stream().map(Map.Entry::getValue).collect(Collectors.toList()))
          .add(closeData)
          .linestyle("-");
        plot.xlabel("Time");
        plot.ylabel("Value");
        plot.title(title);
        plot.show();
      }
    }).start();
  }

  private static NeuralNetwork buildBuyNetwork(Symbol symbol) {
    var dataset = createDataset(symbol, TradeType.BUY, ModelState.TRAINING);
    return NeuralNetwork.create(dataset, "StockAlgorithm",
      INPUT_SIZE_PER_DAY * DAY_REVIEW, new int[] {1024, 1024}, 1);
    /*var iterator = HistoryIterator.create(dataset, BATCH_SIZE,
      TOTAL_BATCHES);
    var network = NeuralNetwork.create(SEED, LEARNING_RATE, DROPOUT_RATE,
      ITERATIONS, EPOCHS, INPUT_SIZE_PER_DAY * DAY_REVIEW, HIDDEN_NEURONS, 1, iterator);
    network.build();
    return network;*/
  }

  private static NeuralNetwork buildSellNetwork(Symbol symbol) {
    var dataset = createDataset(symbol, TradeType.SELL, ModelState.TRAINING);
    return NeuralNetwork.create(dataset, "StockAlgorithm",
      INPUT_SIZE_PER_DAY * DAY_REVIEW, new int[] {1024, 1024}, 1);
    /*var iterator = HistoryIterator.create(dataset, BATCH_SIZE,
      TOTAL_BATCHES);
    var network = NeuralNetwork.create(SEED, LEARNING_RATE, DROPOUT_RATE,
      ITERATIONS, EPOCHS, INPUT_SIZE_PER_DAY * DAY_REVIEW, HIDDEN_NEURONS, 1, iterator);
    network.build();
    return network;*/
  }

  private static List<Map.Entry<List<double[]>, Double>> createDataset(Symbol symbol, TradeType tradeType, ModelState modelState) {
    var result = Lists.<Map.Entry<List<double[]>, Double>>newArrayList();
    var trainData = symbol.findPartOfHistory(TRAIN_DAYS + EVALUATION_DAYS);
    if (modelState == ModelState.TRAINING) {
      trainData = trainData.stream().limit(TRAIN_DAYS).collect(Collectors.toList());
    }
    if (modelState == ModelState.EVALUATING) {
      trainData = trainData.stream().skip(TRAIN_DAYS).collect(Collectors.toList());
    }
    var priceMaximum = trainData.stream().mapToDouble(HistoryEntry::close).max().getAsDouble();
    var maxTrades = modelState == ModelState.TRAINING ? TRAIN_MAX_TRADES : EVALUATION_MAX_TRADES;
    var bestTradeDates = tradeType == TradeType.BUY ?
      findBestBuyDates(trainData, maxTrades) : findBestSellDates(trainData, maxTrades);
    for (var i = 21 + DAY_REVIEW; i < trainData.size(); i++) {
      var output = calculateOutputTradeValue(bestTradeDates, i);
      result.add(new AbstractMap.SimpleEntry<>(createInputData(trainData, priceMaximum, i, output),
        output));
    }
    return result;
  }

  private static double calculateOutputTradeValue(List<Integer> bestDates, int currentDate) {
    for (var date : bestDates) {
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

  private static List<Integer> findBestBuyDates(List<HistoryEntry> history, int maxTrades) {
    return findBestTrades(history, maxTrades).stream()
      .map(trade -> calculateDayFromStep(trade.buyTime() + 1, GENERALISATION_STEP_SIZE))
      .collect(Collectors.toList());
  }

  private static List<Integer> findBestSellDates(List<HistoryEntry> history, int maxTrades) {
    return findBestTrades(history, maxTrades).stream()
      .map(trade -> calculateDayFromStep(trade.sellTime() + 1, GENERALISATION_STEP_SIZE))
      .collect(Collectors.toList());
  }

  private static List<Trade> findBestTrades(List<HistoryEntry> history, int maxTrades) {
    var closeData = history.stream().map(HistoryEntry::close).collect(Collectors.toList());
    var data = Lists.<Double>newArrayList();
    for (var i = 1; i < history.size() / GENERALISATION_STEP_SIZE; i++) {
      var entryIndex = i * GENERALISATION_STEP_SIZE;
      data.add(calculateSMA(closeData, entryIndex, GENERALISATION_STEP_SIZE));
    }
    var allTrades = findAllPossibleTrades(data);
    return filterMostValuableTrades(data, allTrades, maxTrades);
  }

  private static List<Trade> filterMostValuableTrades(
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

  private static List<Trade> findAllPossibleTrades(List<Double> prices) {
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
    return trades;
  }

  private static int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
  }

  private static List<double[]> createInputData(List<HistoryEntry> data, double priceMaximum, int index, double value) {
    var inputData = Lists.<double[]>newArrayList();
    for (var i = 0; i < DAY_REVIEW; i++) {
      inputData.add(createSingleInputData(data, priceMaximum, index - i, value));
    }
    return inputData;
  }

  private static double[] createSingleInputData(List<HistoryEntry> data, double priceMaximum, int index, double value) {
    var dayData = new double[INPUT_SIZE_PER_DAY];
    var closePrices = data.stream().map(HistoryEntry::close).collect(Collectors.toList());
    dayData[0] = calculateChange(closePrices, index, 1);
    dayData[1] = calculateChange(closePrices, index, 2);
    dayData[2] = calculateChange(closePrices, index, 3);
    dayData[3] = (calculateSMA(closePrices, index - 6, 6) / priceMaximum) * 100;
    dayData[4] = (calculateSMA(closePrices, index - 9, 9) / priceMaximum) * 100;
    dayData[5] = (calculateSMA(closePrices, index - 14, 14) / priceMaximum) * 100;
    dayData[6] = (calculateSMA(closePrices, index - 21, 21) / priceMaximum) * 100;
    dayData[7] = calculateROC(closePrices, index, 6);
    dayData[8] = calculateROC(closePrices, index, 9);
    dayData[9] = calculateROC(closePrices, index, 14);
    dayData[10] = calculateROC(closePrices, index, 21);
    dayData[11] = 0;//calculateRSI(closePrices, index, 6);
    dayData[12] = 0;//calculateRSI(closePrices, index, 9);
    dayData[13] = 0;//calculateRSI(closePrices, index, 14);
    dayData[14] = 0;//calculateRSI(closePrices, index, 21);
    dayData[15] = calculateCCI(data, index, 6);
    dayData[16] = calculateCCI(data, index, 9);
    dayData[17] = calculateCCI(data, index, 14);
    dayData[18] = calculateCCI(data, index, 21);
    dayData[19] = calculateSTO(data, index, 6);
    dayData[20] = calculateSTO(data, index, 9);
    dayData[21] = calculateSTO(data, index, 14);
    dayData[22] = calculateSTO(data, index, 21);
    dayData[23] = calculateATR(data, index, 6);
    dayData[24] = calculateATR(data, index, 9);
    dayData[25] = calculateATR(data, index, 14);
    dayData[26] = calculateATR(data, index, 21);
    dayData[27] = determineBullishHammerValue(data, index);
    dayData[28] = determineBearishShootingStarValue(data, index);
    return dayData;
  }

  private static double determineBullishHammerValue(List<HistoryEntry> data, int currentIndex) {
    var entry = data.get(currentIndex);
    var candleLength = entry.high() - entry.low();
    var bodyLength = Math.abs(entry.open() - entry.close());
    var bodyCandleRelation = bodyLength / candleLength;
    var bodyUpperDistance = (entry.high() - entry.open()) / candleLength;
    return ((bodyCandleRelation + bodyUpperDistance) / 2) * 100;
  }

  private static double determineBearishShootingStarValue(List<HistoryEntry> data, int currentIndex) {
    var entry = data.get(currentIndex);
    var candleLength = entry.high() - entry.low();
    var bodyLength = Math.abs(entry.open() - entry.close());
    var bodyCandleRelation = bodyLength / candleLength;
    var bodyLowerDistance = (entry.close() - entry.low()) / candleLength;
    return ((bodyCandleRelation + bodyLowerDistance) / 2) * 100;
  }

  private static double calculateATR(List<HistoryEntry> data, int currentIndex, int lookBack) {
    var accumulatedTR = 0D;
    for (var i = 0; i < lookBack; i++) {
      accumulatedTR += calculateTR(data, currentIndex - i);
    }
    return accumulatedTR / lookBack;
  }

  private static double calculateTR(List<HistoryEntry> data, int currentIndex) {
    return Math.max(data.get(currentIndex).high() - data.get(currentIndex).low(),
      Math.max(Math.abs(data.get(currentIndex).high() - data.get(currentIndex - 1).close()),
        Math.abs(data.get(currentIndex).low() - data.get(currentIndex - 1).close())));
  }

  private static double calculateSTO(List<HistoryEntry> data, int currentIndex, int lookBack) {
    var lowestLow = Double.MAX_VALUE;
    for (var i = 0; i < lookBack; i++) {
      var value = data.get(currentIndex - i).low();
      if (value < lowestLow) {
        lowestLow = value;
      }
    }
    var highestHigh = 0D;
    for (var i = 0; i < lookBack; i++) {
      var value = data.get(currentIndex - i).high();
      if (value > highestHigh) {
        highestHigh = value;
      }
    }
    return ((data.get(currentIndex).close() - lowestLow) / (highestHigh - lowestLow)) * 100;
  }

  private static final float CCI_CONSTANT = 0.015f;

  private static double calculateCCI(List<HistoryEntry> data, int currentIndex, int lookBack) {
    var typicalPrices = Lists.<Double>newArrayList();
    for (var i = 0; i < lookBack; i++) {
      typicalPrices.add(calculateTP(data.get(currentIndex - i)));
    }
    var typicalPriceAverage = calculateSMA(typicalPrices, 0, typicalPrices.size());
    var meanDeviation = calculateMD(typicalPrices, typicalPriceAverage);
    return (calculateTP(data.get(currentIndex)) - typicalPriceAverage) / (CCI_CONSTANT * meanDeviation);
  }

  private static double calculateMD(List<Double> typicalPrices, double typicalPriceAverage) {
    var sum = 0D;
    for (var typicalPrice : typicalPrices) {
      sum += Math.abs(typicalPriceAverage - typicalPrice);
    }
    return sum / typicalPrices.size();
  }

  private static double calculateTP(HistoryEntry entry) {
    return (entry.high() + entry.low() + entry.close()) / 3;
  }

  private static double calculateRSI(List<Double> prices, int currentIndex, int lookBack) {
    return 100 - (100 / (1 + (calculateAverageGain(prices, currentIndex, lookBack) / calculateAverageLoss(prices, currentIndex, lookBack))));
  }

  private static double calculateAverageGain(List<Double> prices, int currentIndex, int lookBack) {
    var totalGain = 0D;
    var individualGains = 0D;
    for (var i = 0; i < lookBack; i++) {
      var currentValue = prices.get(currentIndex - i);
      var previousValue = prices.get(currentIndex - i - 1);
      var change = currentValue - previousValue;
      if (change > 0) {
        totalGain += change;
        individualGains++;
      }
    }
    return totalGain / individualGains;
  }

  private static double calculateAverageLoss(List<Double> prices, int currentIndex, int lookBack) {
    var totalLoss = 0D;
    var individualLooses = 0D;
    for (var i = 0; i < lookBack; i++) {
      var currentValue = prices.get(currentIndex - i);
      var previousValue = prices.get(currentIndex - i - 1);
      var change = currentValue - previousValue;
      if (change < 0) {
        totalLoss += change;
        individualLooses++;
      }
    }
    return Math.abs(totalLoss / individualLooses);
  }

  private static double calculateROC(List<Double> prices, int currentIndex, int lookBack) {
    return 100 * ((prices.get(currentIndex) - prices.get(currentIndex - lookBack)) / prices.get(currentIndex - lookBack));
  }

  private static double calculateSMA(List<Double> prices, int skipDays, int period) {
    double value = 0.0;
    for (int i = skipDays; i < (period + skipDays); i++) {
      value += prices.get(i);
    }
    value /= period;
    return value;
  }

  private static double calculateChange(List<Double> prices, int currentIndex, int offset) {
    return ((prices.get(currentIndex) - prices.get(currentIndex - offset)) / prices.get(currentIndex - 1)) * 100;
  }
}
