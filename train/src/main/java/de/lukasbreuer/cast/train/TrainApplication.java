package de.lukasbreuer.cast.train;

import com.google.inject.Guice;
import de.lukasbreuer.cast.train.dataset.StockDatasetFactory;
import de.lukasbreuer.cast.train.evaluation.EvaluationFactory;
import de.lukasbreuer.cast.train.evaluation.IllustrationFactory;
import de.lukasbreuer.cast.train.neuralnetwork.NeuralNetworkFactory;

public final class TrainApplication {
  public static void main(String[] args) throws Exception {
    var injector = Guice.createInjector(TrainModule.create());
    var stockAlgorithm = TrainAlgorithm.create("WWE", 1,
      injector.getInstance(StockDatasetFactory.class),
      injector.getInstance(NeuralNetworkFactory.class),
      injector.getInstance(EvaluationFactory.class),
      injector.getInstance(IllustrationFactory.class));
    stockAlgorithm.initialize();
    stockAlgorithm.train();
    stockAlgorithm.evaluate();
    stockAlgorithm.save();
  }
}
