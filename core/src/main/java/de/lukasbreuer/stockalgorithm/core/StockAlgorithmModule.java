package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.AbstractModule;
import de.lukasbreuer.stockalgorithm.core.dataset.DatasetModule;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetworkModule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class StockAlgorithmModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Integer.class).toInstance(-1);
    bind(Float.class).toInstance(-1F);
    install(DatasetModule.create());
    install(NeuralNetworkModule.create());
  }
}
