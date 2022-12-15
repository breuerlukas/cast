package de.lukasbreuer.stockalgorithm.train.evaluation;

import com.github.sh0nk.matplotlib4j.Plot;
import com.google.common.collect.Lists;
import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.train.dataset.StockDataset;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public final class Illustration {
  private final TradeType tradeType;
  private final Evaluation evaluation;
  private final StockDataset dataset;
  private final int seed;
  private final int evaluationPeriod;
  private final int generalisationStepSize;

  public void plot() {
    Plot plot = Plot.create();
    addStockEvolution(plot);
    addTradeSignals(plot);
    addTimePredictionGraph(plot);
    label(plot);
    new Thread(() -> display(plot)).start();
  }

  private void addStockEvolution(Plot plot) {
    var prices = dataset.historyData().stream().map(HistoryEntry::close).collect(Collectors.toList());
    var averageData = Lists.<Map.Entry<Integer, Double>>newArrayList();
    for (var i = 1; i < evaluationPeriod / generalisationStepSize; i++) {
      var entryIndex = i * generalisationStepSize;
      averageData.add(new AbstractMap.SimpleEntry<>(calculateDayFromStep(i, generalisationStepSize),
        calculateMovingAverage(prices, entryIndex, generalisationStepSize)));
    }
    plot.plot()
      .add(averageData.stream().map(Map.Entry::getKey).collect(Collectors.toList()),
        averageData.stream().map(Map.Entry::getValue).collect(Collectors.toList()));
    plot.plot().add(prices).label("Prices");
  }

  private static final int ILLUSTRATION_SCALE = 50;

  private void addTradeSignals(Plot plot) {
    for (var optimalSignal : evaluation.optimalSignals()) {
      plot.plot().add(List.of(optimalSignal, optimalSignal),
          List.of(ILLUSTRATION_SCALE / 4, ILLUSTRATION_SCALE + (ILLUSTRATION_SCALE / 4)))
        .label("Optimal");
    }
    for (var determinedSignal : evaluation.determinedSignals()) {
      plot.plot().add(List.of(determinedSignal, determinedSignal),
        List.of(0, ILLUSTRATION_SCALE));
    }
  }

  private void addTimePredictionGraph(Plot plot) {
    var timePredictionEvaluation = evaluation.timePredictionAllocation();
    var dates = Lists.<Integer>newArrayList();
    dates.addAll(timePredictionEvaluation.keySet());
    var predictions = Lists.<Float>newArrayList();
    var minimumPrediction = timePredictionEvaluation.entrySet().stream()
      .min(Map.Entry.comparingByValue()).get().getValue();
    var maximumPrediction = timePredictionEvaluation.entrySet().stream()
      .max(Map.Entry.comparingByValue()).get().getValue();
    for (var prediction : timePredictionEvaluation.values()) {
      predictions.add(ILLUSTRATION_SCALE / 2 + ((prediction - minimumPrediction) /
        (maximumPrediction - minimumPrediction)) * ILLUSTRATION_SCALE / 8);
    }
    plot.plot().add(dates, predictions).label("Prediction Distribution");
  }

  private static final String PLOT_TITLE_FORMAT = "%s (SEED: %s)";

  private void label(Plot plot) {
    plot.plot().linestyle("-");
    plot.xlabel("Time");
    plot.ylabel("Value");
    plot.legend().loc(1);
    plot.title(String.format(PLOT_TITLE_FORMAT,
      tradeType.name().toUpperCase(), seed));
  }

  private void display(Plot plot) {
    try {
      plot.show();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private int calculateDayFromStep(int step, int stepSize) {
    return step * stepSize - (stepSize > 1 ? ((stepSize - 1) / 2) : 0);
  }

  private double calculateMovingAverage(
    List<Double> prices, int skipDays, int period
  ) {
    var value = 0.0D;
    for (var i = skipDays; i < (period + skipDays); i++) {
      if (i < 0 || i >= prices.size()) {
        continue;
      }
      value += prices.get(i);
    }
    value /= period;
    return value;
  }
}
