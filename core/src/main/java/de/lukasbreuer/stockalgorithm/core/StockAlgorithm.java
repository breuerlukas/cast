package de.lukasbreuer.stockalgorithm.core;

import com.clearspring.analytics.util.Lists;
import com.github.sh0nk.matplotlib4j.Plot;
import com.google.common.collect.Maps;
import de.lukasbreuer.stockalgorithm.core.dl4j.HistoryIterator;
import de.lukasbreuer.stockalgorithm.core.dl4j.NeuralNetwork;
import lombok.SneakyThrows;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;
import java.util.stream.Collectors;

public final class StockAlgorithm {
  private static final int SEED = 1;
  private static final float LEARNING_RATE = 0.01f;
  private static final float DROPOUT_RATE = 1f;
  private static final int ITERATIONS = 1;
  private static final int EPOCHS = 100;
  private static final int[] HIDDEN_NEURONS = new int[] {128, 128};
  private static final int INPUT_SIZE_PER_DAY = 29; //29
  private static final int DAY_REVIEW = 28;
  private static final int TRAIN_DAYS = 365 * 6;
  private static final int TRAIN_MAX_TRADES = 6;
  private static final int EVALUATION_DAYS = 365 * 1;
  private static final int EVALUATION_MAX_TRADES = 2;
  private static final int GENERALISATION_STEP_SIZE = 15;
  private static final int BATCH_SIZE = 10;
  private static final int TOTAL_BATCHES = 200;
  private static final String STOCK = "AAPL";

  //TODO: CODE CLEAN UP (ESPECIALLY StockAlgorithm)
  //TODO: FIX: BUY & SELL NETWORKS PRODUCE SAME SIGNALS (PROBLEM WITH NORMALIZATION)

  public static void main(String[] args) throws Exception {
    Nd4j.getRandom().setSeed(SEED);
    var symbol = Symbol.createAndFetch(STOCK);
    var buyNetwork = buildBuyNetwork(symbol);
    var sellNetwork = buildSellNetwork(symbol);
    var closeData = symbol.findPartOfHistory(TRAIN_DAYS + EVALUATION_DAYS).stream().skip(TRAIN_DAYS).collect(Collectors.toList());
    System.out.println(findBestTrades(closeData, EVALUATION_MAX_TRADES));
    System.out.println("TRAINING");
    System.out.println("");
    System.out.println("TRAIN BUY NETWORK");
    var buyEvaluationDataset = createDataset(symbol, TradeType.BUY, ModelState.EVALUATING);
    System.out.println(Arrays.toString(buyEvaluationDataset.get(1).getKey().get(0)));
    buyNetwork.train(buyEvaluationDataset.stream().filter(entry -> entry.getValue() == 1).findFirst().get());
    System.out.println("EVALUATE BUY NETWORK");
    displayGraph(symbol, buyNetwork, buyEvaluationDataset, 0.1f, 200, "BUY");
    System.out.println("TRAIN SELL NETWORK");
    var sellEvaluationDataset = createDataset(symbol, TradeType.SELL, ModelState.EVALUATING);
    System.out.println(Arrays.toString(sellEvaluationDataset.get(1).getKey().get(0)));
    sellNetwork.train(sellEvaluationDataset.stream().filter(entry -> entry.getValue() == 1).findFirst().get());
    System.out.println("EVALUATE SELL NETWORK");
    displayGraph(symbol, sellNetwork, sellEvaluationDataset, 0.1f, 200, "SELL");
  }

  private static void displayGraph(Symbol symbol, NeuralNetwork network, List<Map.Entry<List<double[]>, Double>> dataset, float minPrediction, int scale, String title) throws Exception {
    Plot plot = Plot.create();

    var allValues = Maps.<Integer, Float>newHashMap();

    for (var i = 1; i < dataset.size() - 1; i++) {
      var drawIndex = i + 21 + DAY_REVIEW;
      allValues.put(drawIndex, network.evaluate(drawIndex, dataset.get(i)));
      /*if (network.evaluate(drawIndex, dataset.get(i)) > minPrediction && (network.evaluate(drawIndex - 1, dataset.get(i - 1)) > 0.0 || network.evaluate(drawIndex + 1, dataset.get(i + 1)) > 0.0)) {
        plot.plot().add(List.of(drawIndex, drawIndex), List.of(0, scale));
        System.out.println("Add Line " + i + " To Graph");
      }*/
      if (dataset.get(i).getValue() == 1) {
        plot.plot().add(List.of(drawIndex, drawIndex), List.of(scale / 4, scale + (scale / 4)));
      }
    }

    System.out.println("Highest values: ");

    var addedTrades = Lists.<Integer>newArrayList();

    for (var entry : allValues.entrySet().stream().sorted(Map.Entry.<Integer, Float>comparingByValue().reversed()).collect(Collectors.toList())) {
      /*var isTooClose = false;
      for (var addedTrade : addedTrades) {
        if (Math.abs(entry.getKey() - addedTrade) < 20) {
          isTooClose = true;
          break;
        }
      }
      if (isTooClose) {
        continue;
      }*/
      plot.plot().add(List.of(entry.getKey(), entry.getKey()), List.of(0, scale));
      addedTrades.add(entry.getKey());
      System.out.println(entry.getKey() + ": " + entry.getValue());
      if (addedTrades.size() == EVALUATION_MAX_TRADES) {
        break;
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

  private static NeuralNetwork buildBuyNetwork(Symbol symbol) throws Exception {
    var dataset = createDataset(symbol, TradeType.BUY, ModelState.TRAINING);
    var iterator = HistoryIterator.create(dataset, new Random(SEED), BATCH_SIZE, TOTAL_BATCHES);
    var network = NeuralNetwork.create(SEED, LEARNING_RATE, DROPOUT_RATE,
      ITERATIONS, EPOCHS, INPUT_SIZE_PER_DAY * DAY_REVIEW, HIDDEN_NEURONS, 2, iterator);
    network.build();
    return network;
  }

  private static NeuralNetwork buildSellNetwork(Symbol symbol) throws Exception {
    var dataset = createDataset(symbol, TradeType.SELL, ModelState.TRAINING);
    var iterator = HistoryIterator.create(dataset, new Random(SEED), BATCH_SIZE, TOTAL_BATCHES);
    var network = NeuralNetwork.create(SEED, LEARNING_RATE, DROPOUT_RATE,
      ITERATIONS, EPOCHS, INPUT_SIZE_PER_DAY * DAY_REVIEW, HIDDEN_NEURONS, 2, iterator);
    network.build();
    return network;
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
      var entry = new AbstractMap.SimpleEntry<>(createInputData(trainData, priceMaximum, i),
        calculateOutputTradeValue(bestTradeDates, i));
      result.add(entry);
    }
    return result; //normalizeData(result);
  }

  /*private static List<Map.Entry<List<double[]>, Double>> normalizeData(List<Map.Entry<List<double[]>, Double>> data) {
    var normalizedData = Lists.<Map.Entry<List<double[]>, Double>>newArrayList();
    double[] maximums = new double[INPUT_SIZE_PER_DAY];
    Arrays.fill(maximums, -Double.MAX_VALUE);
    double[] minimums = new double[INPUT_SIZE_PER_DAY];
    Arrays.fill(minimums, Double.MAX_VALUE);
    for (var entry : data) {
      for (var dayInputData : entry.getKey()) {
        for (var i = 0; i < INPUT_SIZE_PER_DAY; i++) {
          if (dayInputData[i] > maximums[i]) {
            maximums[i] = dayInputData[i];
          }
          if (dayInputData[i] < minimums[i]) {
            minimums[i] = dayInputData[i];
          }
        }
      }
    }
    for (var entry : data) {
      var inputData = Lists.<double[]>newArrayList();
      for (var dayInputData : entry.getKey()) {
        var dayNormalizedInput = new double[INPUT_SIZE_PER_DAY];
        for (var i = 0; i < INPUT_SIZE_PER_DAY; i++) {
          if (maximums[i] == -Double.MAX_VALUE || minimums[i] == Double.MAX_VALUE || (maximums[i] - minimums[i]) == 0) {
            continue;
          }
          dayNormalizedInput[i] = (dayInputData[i] - minimums[i]) / (maximums[i] - minimums[i]);
        }
        inputData.add(dayNormalizedInput);
      }
      normalizedData.add(new AbstractMap.SimpleEntry<>(inputData, entry.getValue()));
    }
    return normalizedData;
  }*/

  private static double calculateOutputTradeValue(List<Integer> bestDates, int currentDate) {
    for (var date : bestDates) {
      if (currentDate == date) {
        return 1;
      }
      if (currentDate == date + 1 || currentDate == date - 1) {
        return 0.9;
      }
      if (currentDate == date + 2 || currentDate == date - 2) {
        return 0.9;
      }
      if (currentDate == date + 3 || currentDate == date - 3) {
        return 0.9;
      }
      if (currentDate == date + 4 || currentDate == date - 4) {
        return 0.9;
      }
    }
    return 0;
  }

  private static List<Integer> findBestBuyDates(List<HistoryEntry> history, int maxTrades) {
    return findBestTrades(history, maxTrades).stream()
      .map(Trade::buyTime)
      .collect(Collectors.toList());
  }

  private static List<Integer> findBestSellDates(List<HistoryEntry> history, int maxTrades) {
    return findBestTrades(history, maxTrades).stream()
      .map(Trade::sellTime)
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
    var noiselessTrades = filterNoiseOutOfTrades(allTrades, closeData);
    var collapsedTrades = collapseIntersectingTrades(noiselessTrades);
    return filterMostValuableTrades(closeData, collapsedTrades, maxTrades);
  }

  private static List<Trade> collapseIntersectingTrades(List<Trade> trades) {
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

  private static List<Trade> filterNoiseOutOfTrades(List<Trade> trades, List<Double> closeData) {
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

  private static int calculateNoiselessSignal(Trade trade, List<Double> closeData, TradeType type) {
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

  private static int determineOptimalSignal(
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

  private static int calculateDirectionalNoiselessSignal(Trade trade, List<Double> closeData, TradeType type, int stepSize, int direction) {
    var initialSignal = type == TradeType.BUY ? trade.buyTime() : trade.sellTime();
    var lastAverage = calculateSMA(closeData, initialSignal - ((stepSize - 1) / 2) - direction, stepSize);
    var calculationLength = direction > 0 ? Math.min(closeData.size() - initialSignal - stepSize, 30) :
      Math.min(initialSignal - 30 - stepSize, 30);
    for (var i = 0; i < calculationLength; i++) {
      var average = calculateSMA(closeData, initialSignal - ((stepSize - 1) / 2) + i * direction, stepSize);
      if (type == TradeType.BUY ? average > lastAverage : average < lastAverage) {
        return initialSignal + i * direction;
      }
      lastAverage = average;
    }
    return initialSignal;
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
    return trades.stream()
      .map(trade -> Trade.create(calculateDayFromStep(trade.buyTime() + 1, GENERALISATION_STEP_SIZE),
        calculateDayFromStep(trade.sellTime() + 1, GENERALISATION_STEP_SIZE)))
      .collect(Collectors.toList());
  }

  private static int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
  }

  private static List<double[]> createInputData(List<HistoryEntry> data, double priceMaximum, int index) {
    var inputData = Lists.<double[]>newArrayList();
    for (var i = 0; i < DAY_REVIEW; i++) {
      inputData.add(createSingleInputData(data.stream().limit(index - i + 1).collect(Collectors.toList()), priceMaximum, index - i));
    }
    return inputData;
  }

  private static double[] createSingleInputData(List<HistoryEntry> data, double priceMaximum, int index) {
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
    dayData[11] = calculateRSI(closePrices, index, 6);
    dayData[12] = calculateRSI(closePrices, index, 9);
    dayData[13] = calculateRSI(closePrices, index, 14);
    dayData[14] = calculateRSI(closePrices, index, 21);
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
    if (individualGains == 0) {
      return 0;
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
    if (individualLooses == 0) {
      return 0;
    }
    return Math.abs(totalLoss / individualLooses);
  }

  private static double calculateROC(List<Double> prices, int currentIndex, int lookBack) {
    return 100 * ((prices.get(currentIndex) - prices.get(currentIndex - lookBack)) / prices.get(currentIndex - lookBack));
  }

  private static double calculateSMA(List<Double> prices, int skipDays, int period) {
    double value = 0.0;
    for (int i = skipDays; i < (period + skipDays); i++) {
      if (i < 0 || i >= prices.size()) {
        continue;
      }
      value += prices.get(i);
    }
    value /= period;
    return value;
  }

  private static double calculateChange(List<Double> prices, int currentIndex, int offset) {
    return ((prices.get(currentIndex) - prices.get(currentIndex - offset)) / prices.get(currentIndex - 1)) * 100;
  }
}
