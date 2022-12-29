package de.lukasbreuer.cast.train;

import com.google.inject.AbstractModule;
import de.lukasbreuer.cast.core.CoreModule;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.train.dataset.DatasetModule;
import de.lukasbreuer.cast.train.neuralnetwork.NeuralNetworkModule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class TrainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(CoreModule.create());
    install(DatasetModule.create());
    install(NeuralNetworkModule.create());
    try {
      bind(Log.class).toInstance(Log.create("Train"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
