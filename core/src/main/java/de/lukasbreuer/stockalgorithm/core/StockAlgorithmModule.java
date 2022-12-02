package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.AbstractModule;
import de.lukasbreuer.stockalgorithm.core.dataset.DatasetModule;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetworkModule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class StockAlgorithmModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(int.class).toInstance(-1);
    bind(float.class).toInstance(-1F);
    bind(int[].class).toInstance(new int[0]);
    install(DatasetModule.create());
    install(NeuralNetworkModule.create());
  }
}
