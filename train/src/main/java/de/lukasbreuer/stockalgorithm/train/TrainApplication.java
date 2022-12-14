package de.lukasbreuer.stockalgorithm.train;

import com.google.inject.Guice;
import de.lukasbreuer.stockalgorithm.train.dataset.StockDatasetFactory;
import de.lukasbreuer.stockalgorithm.train.evaluation.EvaluationFactory;
import de.lukasbreuer.stockalgorithm.train.evaluation.IllustrationFactory;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetworkFactory;

public final class TrainApplication {
  public static void main(String[] args) throws Exception {
    var injector = Guice.createInjector(StockAlgorithmModule.create());
    var stockAlgorithm = TrainAlgorithm.create("NEE", 1,
      injector.getInstance(StockDatasetFactory.class),
      injector.getInstance(NeuralNetworkFactory.class),
      injector.getInstance(EvaluationFactory.class),
      injector.getInstance(IllustrationFactory.class));
    stockAlgorithm.initialize();
    stockAlgorithm.train();
    stockAlgorithm.evaluate();
  }
}
