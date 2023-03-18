package de.lukasbreuer.cast.train;

import com.google.inject.Guice;
import de.lukasbreuer.cast.core.dataset.StockDatasetFactory;
import de.lukasbreuer.cast.train.evaluation.EvaluationFactory;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetworkFactory;

public final class TrainApplication {
  public static void main(String[] args) {
    var injector = Guice.createInjector(TrainModule.create());
    var stockAlgorithm = TrainAlgorithm.create("ES", 1,
      injector.getInstance(StockDatasetFactory.class),
      injector.getInstance(NeuralNetworkFactory.class),
      injector.getInstance(EvaluationFactory.class));
    stockAlgorithm.initialize(stockAlgorithm::processBuyNetwork);
    while (true) {

    }
  }
}
