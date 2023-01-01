package de.lukasbreuer.cast.train;

import de.lukasbreuer.cast.core.CoreModule;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.train.dataset.DatasetModule;
import de.lukasbreuer.cast.train.neuralnetwork.NeuralNetworkModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class TrainModule extends CoreModule {
  public static TrainModule create() {
    return new TrainModule();
  }

  @Override
  protected void configure() {
    super.configure();
    install(DatasetModule.create());
    install(NeuralNetworkModule.create());
  }

  @Override
  protected void configureLog() {
    try {
      bind(Log.class).toInstance(Log.create("Train", "/logs/train/"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
