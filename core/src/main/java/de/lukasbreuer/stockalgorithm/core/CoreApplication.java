package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.Guice;
import de.lukasbreuer.stockalgorithm.core.dataset.StockDatasetFactory;
import de.lukasbreuer.stockalgorithm.core.evaluation.EvaluationFactory;
import de.lukasbreuer.stockalgorithm.core.evaluation.IllustrationFactory;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetworkFactory;

public final class CoreApplication {
  public static void main(String[] args) throws Exception {
    var injector = Guice.createInjector(StockAlgorithmModule.create());
    var stockAlgorithm = StockAlgorithm.create("ATVI", 100,
      injector.getInstance(StockDatasetFactory.class),
      injector.getInstance(NeuralNetworkFactory.class),
      injector.getInstance(EvaluationFactory.class),
      injector.getInstance(IllustrationFactory.class));
    stockAlgorithm.initialize();
    stockAlgorithm.train();
    stockAlgorithm.evaluate();
  }
}
