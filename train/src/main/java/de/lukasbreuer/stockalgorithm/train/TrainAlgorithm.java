package de.lukasbreuer.stockalgorithm.train;

import de.lukasbreuer.stockalgorithm.core.dataset.StockDataset;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.ModelState;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.stockalgorithm.core.symbol.Symbol;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import de.lukasbreuer.stockalgorithm.train.dataset.StockDatasetFactory;
import de.lukasbreuer.stockalgorithm.train.evaluation.EvaluationFactory;
import de.lukasbreuer.stockalgorithm.train.evaluation.IllustrationFactory;
import de.lukasbreuer.stockalgorithm.train.neuralnetwork.NeuralNetworkFactory;
import lombok.RequiredArgsConstructor;
import org.nd4j.linalg.factory.Nd4j;

@RequiredArgsConstructor(staticName = "create")
public final class TrainAlgorithm {
  private final String symbolName;
  private final int seed;
  private final StockDatasetFactory datasetFactory;
  private final NeuralNetworkFactory neuralNetworkFactory;
  private final EvaluationFactory evaluationFactory;
  private final IllustrationFactory illustrationFactory;
  private Symbol symbol;
  private NeuralNetwork buyNeuralNetwork;
  private NeuralNetwork sellNeuralNetwork;
  private StockDataset buyEvaluationDataset;
  private StockDataset sellEvaluationDataset;

  public void initialize() throws Exception {
    Nd4j.getRandom().setSeed(seed);
    symbol = Symbol.createAndFetch(symbolName);
    buyNeuralNetwork = buildNeuralNetwork(TradeType.BUY);
    sellNeuralNetwork = buildNeuralNetwork(TradeType.SELL);
    buyEvaluationDataset = datasetFactory.createAndBuild(symbol, TradeType.BUY, ModelState.EVALUATING, seed);
    sellEvaluationDataset = datasetFactory.createAndBuild(symbol, TradeType.SELL, ModelState.EVALUATING, seed);
  }

  private NeuralNetwork buildNeuralNetwork(TradeType tradeType) {
    var dataset = datasetFactory.createAndBuild(symbol, tradeType, ModelState.TRAINING, seed);
    var network = neuralNetworkFactory.create(dataset.historyIterator(), seed);
    network.build();
    return network;
  }

  public void train() {
    System.out.println("TRAINING");
    System.out.println("");
    System.out.println("TRAIN BUY NETWORK");
    buyNeuralNetwork.train(buyEvaluationDataset.raw().stream()
      .filter(entry -> entry.getValue() == 1).findFirst().get());
    /*System.out.println("TRAIN SELL NETWORK");
    sellNeuralNetwork.train(sellEvaluationDataset.raw().stream()
      .filter(entry -> entry.getValue() == 1).findFirst().get());*/
  }

  public void evaluate() {
    System.out.println("EVALUATING");
    System.out.println("");
    System.out.println("EVALUATE BUY NETWORK");
    var buyEvaluation = evaluationFactory.create(buyNeuralNetwork, buyEvaluationDataset);
    buyEvaluation.analyse();
    var buyIllustration = illustrationFactory.create(TradeType.BUY, buyEvaluation, buyEvaluationDataset, seed);
    buyIllustration.plot();
    /*System.out.println("EVALUATE SELL NETWORK");
    var sellEvaluation = evaluationFactory.create(sellNeuralNetwork, sellEvaluationDataset);
    sellEvaluation.analyse();
    var sellIllustration = illustrationFactory.create(TradeType.SELL, sellEvaluation, sellEvaluationDataset, seed);
    sellIllustration.plot();*/
  }

  private final String MODEL_PATH = "/models/";

  public void save() throws Exception {
    buyNeuralNetwork.save(System.getProperty("user.dir") + MODEL_PATH +
      symbolName.toUpperCase() + "/buyModel.zip");
    sellNeuralNetwork.save(System.getProperty("user.dir") + MODEL_PATH +
      symbolName.toUpperCase() + "/sellModel.zip");
  }
}
