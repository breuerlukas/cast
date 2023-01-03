package de.lukasbreuer.cast.deploy.model;

import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.core.neuralnetwork.ModelState;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Accessors(fluent = true)
public final class Model {
  public static Model of(Log log, Document document) {
    return create(log, UUID.fromString(document.getString("id")),
      document.getString("stock"), document.getString("buyModelPath"),
      document.getString("sellModelPath"), document.getInteger("reviewPeriod"),
      document.getDouble("buyTradePredictionMinimum"),
      document.getDouble("sellTradePredictionMinimum"));
  }

  public static Model create(
    Log log, UUID id, String stock, String buyModelPath, String sellModelPath,
    int reviewPeriod, double buyTradePredictionMinimum,
    double sellTradePredictionMinimum
  ) {
    return new Model(log, id, stock, buyModelPath, sellModelPath, reviewPeriod,
      buyTradePredictionMinimum, sellTradePredictionMinimum);
  }

  private Log log;
  @Getter
  private UUID id;
  private String stock;
  @Getter @Setter(AccessLevel.PRIVATE)
  private String buyModelPath;
  @Getter @Setter(AccessLevel.PRIVATE)
  private String sellModelPath;
  @Getter @Setter(AccessLevel.PRIVATE)
  private int reviewPeriod;
  @Getter @Setter(AccessLevel.PRIVATE)
  private double buyTradePredictionMinimum;
  @Getter @Setter(AccessLevel.PRIVATE)
  private double sellTradePredictionMinimum;
  private Symbol symbol;
  private NeuralNetwork buyNeuralNetwork;
  private NeuralNetwork sellNeuralNetwork;

  private Model(
    Log log, UUID id, String stock, String buyModelPath, String sellModelPath,
    int reviewPeriod, double buyTradePredictionMinimum,
    double sellTradePredictionMinimum
  ) {
    this.log = log;
    this.id = id;
    this.stock = stock;
    this.buyModelPath = buyModelPath;
    this.sellModelPath = sellModelPath;
    this.reviewPeriod = reviewPeriod;
    this.buyTradePredictionMinimum = buyTradePredictionMinimum;
    this.sellTradePredictionMinimum = sellTradePredictionMinimum;
  }

  public void initialize() {
    try {
      symbol = Symbol.createAndFetch(stock, -1);
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
      predictions[i] = tradeType.isBuy() ? buyNeuralNetwork.evaluate(0, dataset.raw().get(i), false) :
        sellNeuralNetwork.evaluate(0, dataset.raw().get(i), false);
    }
    return predictions;
  }

  public double currentStockPrice() {
    return symbol.findPartOfHistory(1).get(0).close();
  }

  public void updateParameter(String parameter, String value) {
    switch (parameter.toLowerCase()) {
      case "buymodelpath" -> buyModelPath(value);
      case "sellmodelpath" -> sellModelPath(value);
      case "reviewperiod" -> reviewPeriod(Integer.parseInt(value));
      case "buytradepredictionminimum" -> buyTradePredictionMinimum(Double.parseDouble(value));
      case "selltradepredictionminimum" -> sellTradePredictionMinimum(Double.parseDouble(value));
    }
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("stock", stock);
    document.append("buyModelPath", buyModelPath);
    document.append("sellModelPath", sellModelPath);
    document.append("reviewPeriod", reviewPeriod);
    document.append("buyTradePredictionMinimum", buyTradePredictionMinimum);
    document.append("sellTradePredictionMinimum", sellTradePredictionMinimum);
    return document;
  }
}
