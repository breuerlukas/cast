package de.lukasbreuer.stockalgorithm.deploy.model;

import de.lukasbreuer.stockalgorithm.core.dataset.StockDataset;
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
  public static Model of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("stock"), document.getString("modelPath"),
      document.getInteger("reviewPeriod"));
  }

  @Getter
  private final UUID id;
  private final String stock;
  private final String modelPath;
  private final int reviewPeriod;
  private Symbol symbol;
  private NeuralNetwork neuralNetwork;

  public void initialize() throws Exception {
    symbol = Symbol.createAndFetch(stock);
    neuralNetwork = NeuralNetwork.createAndLoad(modelPath);
  }

  private static final int INPUT_SIZE_PER_DAY = 42;
  private static final int DAY_LONGEST_REVIEW = 21;

  public float[] predict(TradeType tradeType, int timeSpan) {
    var dataset = StockDataset.create(symbol, tradeType, ModelState.EVALUATING,
      0, 0, 0, timeSpan, 0, reviewPeriod, 0, 0, 0, 0, INPUT_SIZE_PER_DAY,
      DAY_LONGEST_REVIEW);
    dataset.build();
    var predictions = new float[timeSpan];
    for (var i = 0; i < timeSpan; i++) {
      predictions[i] = neuralNetwork.evaluate(0, dataset.raw().get(i));
    }
    return predictions;
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("stock", stock);
    document.append("modelPath", modelPath);
    document.append("reviewPeriod", reviewPeriod);
    return document;
  }
}
