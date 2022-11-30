package de.lukasbreuer.stockalgorithm.core;

import com.clearspring.analytics.util.Lists;
import com.github.sh0nk.matplotlib4j.Plot;
import com.google.common.collect.Maps;
import de.lukasbreuer.stockalgorithm.core.dataset.HistoryIterator;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.ModelState;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import de.lukasbreuer.stockalgorithm.core.symbol.Symbol;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.SneakyThrows;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;
import java.util.stream.Collectors;

public final class StockAlgorithm {
  private static final float LEARNING_RATE = 0.01f;
  private static final float DROPOUT_RATE = 1f;
  private static final int ITERATIONS = 1;
  private static final int EPOCHS = 10;
  private static final int[] HIDDEN_NEURONS = new int[] {32, 32};
  private static final int INPUT_SIZE_PER_DAY = 29; //29
  private static final int DAY_REVIEW = 7;
  private static final int TRAIN_DAYS = 365 * 4;
  private static final int TRAIN_MAX_TRADES = 8;
  private static final int EVALUATION_DAYS = 365 * 1;
  private static final int EVALUATION_MAX_TRADES = 2;
  private static final int GENERALISATION_STEP_SIZE = 15;
  private static final int BATCH_SIZE = 10;
  private static final int TOTAL_BATCHES = TRAIN_DAYS;
  private static final String STOCK = "AAPL";
  private static int seed = 1;

  //TODO: CODE CLEAN UP (ESPECIALLY StockAlgorithm)
  //TODO: FIX: BUY & SELL NETWORKS PRODUCE SAME SIGNALS (PROBLEM WITH NORMALIZATION)

  public static void main(String[] args) throws Exception {
    beginSimultaneously();
  }

  private static void beginSimultaneously() throws Exception {
    for (var i = 0; i < 20; i++) {
      begin();
      seed += 1;
    }
  }

  private static void begin() throws Exception {
    Nd4j.getRandom().setSeed(seed);
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
    /*System.out.println("TRAIN SELL NETWORK");
    var sellEvaluationDataset = createDataset(symbol, TradeType.SELL, ModelState.EVALUATING);
    System.out.println(Arrays.toString(sellEvaluationDataset.get(1).getKey().get(0)));
    sellNetwork.train(sellEvaluationDataset.stream().filter(entry -> entry.getValue() == 1).findFirst().get());
    System.out.println("EVALUATE SELL NETWORK");
    displayGraph(symbol, sellNetwork, sellEvaluationDataset, 0.1f, 200, "SELL");*/
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

    var currentSeed = seed;

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
        plot.title(title + " (SEED: " + currentSeed + ")");
        plot.show();
      }
    }).start();
  }

  private static NeuralNetwork buildBuyNetwork(Symbol symbol) throws Exception {
    var dataset = createDataset(symbol, TradeType.BUY, ModelState.TRAINING);
    var tradeSignals = dataset.stream().filter(entry -> entry.getValue() > 0).collect(Collectors.toList());
    for (var i = 0; i < 4; i++) {
      dataset.addAll(tradeSignals);
    }
    var iterator = HistoryIterator.create(dataset, new Random(sesed), BATCH_SIZE, TOTAL_BATCHES);
    var network = NeuralNetwork.create(seed, LEARNING_RATE, DROPOUT_RATE,
      ITERATIONS, EPOCHS, INPUT_SIZE_PER_DAY * DAY_REVIEW, HIDDEN_NEURONS, 2, iterator);
    network.build();
    return network;
  }

  private static NeuralNetwork buildSellNetwork(Symbol symbol) throws Exception {
    var dataset = createDataset(symbol, TradeType.SELL, ModelState.TRAINING);
    var iterator = HistoryIterator.create(dataset, new Random(seed), BATCH_SIZE, TOTAL_BATCHES);
    var network = NeuralNetwork.create(seed, LEARNING_RATE, DROPOUT_RATE,
      ITERATIONS, EPOCHS, INPUT_SIZE_PER_DAY * DAY_REVIEW, HIDDEN_NEURONS, 2, iterator);
    network.build();
    return network;
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

  private static int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
  }
}
