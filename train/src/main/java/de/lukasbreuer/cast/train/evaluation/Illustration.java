package de.lukasbreuer.cast.train.evaluation;

import com.github.sh0nk.matplotlib4j.Plot;
import com.google.common.collect.Lists;
import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public final class Illustration {
  private final TradeType tradeType;
  private final Evaluation evaluation;
  private final StockDataset dataset;
  private final int seed;

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
    plot.plot().add(prices).label("Prices").color("blue");
  }

  private static final int ILLUSTRATION_SCALE = 75;

  private void addTradeSignals(Plot plot) {
    for (var i = 0; i < evaluation.optimalSignals().size(); i++) {
      var optimalSignal = evaluation.optimalSignals().get(i);
      var line = plot.plot().add(List.of(optimalSignal, optimalSignal),
          List.of(ILLUSTRATION_SCALE / 4, ILLUSTRATION_SCALE + (ILLUSTRATION_SCALE / 4)))
        .color("green");
      if (i == evaluation.optimalSignals().size() - 1) {
        line.label("Optimal");
      }
    }
    for (var determinedSignal : evaluation.determinedSignals()) {
      plot.plot().add(List.of(determinedSignal, determinedSignal),
        List.of(0, ILLUSTRATION_SCALE)).color("red");
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
    plot.plot().add(dates, predictions).label("Prediction Distribution").color("magenta");
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
}
