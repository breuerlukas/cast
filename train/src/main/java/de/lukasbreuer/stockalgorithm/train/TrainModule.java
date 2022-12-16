package de.lukasbreuer.stockalgorithm.train;

import com.google.inject.AbstractModule;
import de.lukasbreuer.stockalgorithm.core.CoreModule;
import de.lukasbreuer.stockalgorithm.train.dataset.DatasetModule;
import de.lukasbreuer.stockalgorithm.train.neuralnetwork.NeuralNetworkModule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class TrainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(CoreModule.create());
    install(DatasetModule.create());
    install(NeuralNetworkModule.create());
  }
}
