package de.lukasbreuer.cast.train;

import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.neuralnetwork.ModelState;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.TradeType;
import de.lukasbreuer.cast.train.dataset.StockDatasetFactory;
import de.lukasbreuer.cast.train.evaluation.EvaluationFactory;
import de.lukasbreuer.cast.train.evaluation.IllustrationFactory;
import de.lukasbreuer.cast.train.neuralnetwork.NeuralNetworkFactory;
import lombok.RequiredArgsConstructor;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;

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

  public void initialize() {
    Nd4j.getRandom().setSeed(seed);
    symbol = Symbol.createAndFetch(symbolName, -1);
  }

  public void processBuyNetwork() throws Exception {
    initializeBuyNetwork();
    trainBuyNetwork();
    evaluateBuyNetwork();
    saveBuyNetwork();
  }

  public void processSellNetwork() throws Exception {
    initializeSellNetwork();
    trainSellNetwork();
    evaluateSellNetwork();
    saveSellNetwork();
  }

  private void initializeBuyNetwork() {
    buyNeuralNetwork = buildNeuralNetwork(TradeType.BUY);
    buyEvaluationDataset = datasetFactory.createAndBuild(symbol, TradeType.BUY, ModelState.EVALUATING, seed);
  }

  private void initializeSellNetwork() {
    sellNeuralNetwork = buildNeuralNetwork(TradeType.SELL);
    sellEvaluationDataset = datasetFactory.createAndBuild(symbol, TradeType.SELL, ModelState.EVALUATING, seed);
  }

  private NeuralNetwork buildNeuralNetwork(TradeType tradeType) {
    var dataset = datasetFactory.createAndBuild(symbol, tradeType, ModelState.TRAINING, seed);
    var network = neuralNetworkFactory.create(dataset.historyIterator(), seed);
    network.build();
    var evaluation = evaluationFactory.create(network, dataset);
    evaluation.analyse();
    var illustration = illustrationFactory.create(tradeType, evaluation, dataset, seed);
    illustration.plot();
    System.out.println(Arrays.toString(dataset.raw().get(0).getKey().get(0)));
    return network;
  }

  private void trainBuyNetwork() {
    System.out.println("TRAIN BUY NETWORK");
    buyNeuralNetwork.train();
  }

  private void trainSellNetwork() {
    System.out.println("TRAIN SELL NETWORK");
    sellNeuralNetwork.train();
  }

  private void evaluateBuyNetwork() {
    System.out.println("EVALUATE BUY NETWORK");
    var buyEvaluation = evaluationFactory.create(buyNeuralNetwork, buyEvaluationDataset);
    buyEvaluation.analyse();
    var buyIllustration = illustrationFactory.create(TradeType.BUY, buyEvaluation, buyEvaluationDataset, seed);
    buyIllustration.plot();
  }

  private void evaluateSellNetwork() {
    System.out.println("EVALUATE SELL NETWORK");
    var sellEvaluation = evaluationFactory.create(sellNeuralNetwork, sellEvaluationDataset);
    sellEvaluation.analyse();
    var sellIllustration = illustrationFactory.create(TradeType.SELL, sellEvaluation, sellEvaluationDataset, seed);
    sellIllustration.plot();
  }

  private final String MODEL_PATH = "/models/";

  private void saveBuyNetwork() throws Exception {
    buyNeuralNetwork.save(System.getProperty("user.dir") + MODEL_PATH +
      symbolName.toUpperCase() + "/buyModel.zip");
  }

  private void saveSellNetwork() throws Exception {
    sellNeuralNetwork.save(System.getProperty("user.dir") + MODEL_PATH +
      symbolName.toUpperCase() + "/sellModel.zip");
  }
}
