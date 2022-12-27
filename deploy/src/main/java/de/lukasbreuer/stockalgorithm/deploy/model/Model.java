package de.lukasbreuer.stockalgorithm.deploy.model;

import de.lukasbreuer.stockalgorithm.core.dataset.StockDataset;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.ModelState;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.stockalgorithm.core.symbol.Symbol;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Model {
  public static Model of(Log log, Document document) {
    return create(log, UUID.fromString(document.getString("id")),
      document.getString("stock"), document.getString("buyModelPath"),
      document.getString("sellModelPath"), document.getInteger("reviewPeriod"));
  }

  private final Log log;
  @Getter
  private final UUID id;
  private final String stock;
  private final String buyModelPath;
  private final String sellModelPath;
  private final int reviewPeriod;
  private Symbol symbol;
  private NeuralNetwork buyNeuralNetwork;
  private NeuralNetwork sellNeuralNetwork;

  public void initialize() {
    try {
      symbol = Symbol.createAndFetch(stock);
      var executionPath = System.getProperty("user.dir");
      buyNeuralNetwork = NeuralNetwork.createAndLoad(executionPath + buyModelPath);
      sellNeuralNetwork = NeuralNetwork.createAndLoad(executionPath + sellModelPath);
      log.info("The " + stock.toUpperCase() + " model has been successfully " +
        "loaded and initialized");
    } catch (Exception exception) {
      log.severe("Initialization of the " + stock.toUpperCase() + " model failed");
      exception.printStackTrace();
    }
  }

  private static final int INPUT_SIZE_PER_DAY = 42;
  private static final int DAY_LONGEST_REVIEW = 21;

  public float[] predict(TradeType tradeType, int timeSpan) {
    var dataset = StockDataset.create(symbol, tradeType, ModelState.EVALUATING,
      0, 0, 0, timeSpan + DAY_LONGEST_REVIEW + reviewPeriod, 0, reviewPeriod,
      0, 0, 1, 1, INPUT_SIZE_PER_DAY, DAY_LONGEST_REVIEW);
    dataset.build();
    var predictions = new float[timeSpan];
    for (var i = 0; i < timeSpan; i++) {
      predictions[i] = tradeType.isBuy() ? buyNeuralNetwork.evaluate(0, dataset.raw().get(i)) :
        sellNeuralNetwork.evaluate(0, dataset.raw().get(i));
    }
    return predictions;
  }

  public double currentStockPrice() {
    return symbol.findPartOfHistory(1).get(0).close();
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("stock", stock);
    document.append("buyModelPath", buyModelPath);
    document.append("sellModelPath", sellModelPath);
    document.append("reviewPeriod", reviewPeriod);
    return document;
  }
}
