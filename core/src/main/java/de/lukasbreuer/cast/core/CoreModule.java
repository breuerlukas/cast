package de.lukasbreuer.cast.core;

import com.google.inject.AbstractModule;
import de.lukasbreuer.cast.core.database.DatabaseModule;
import de.lukasbreuer.cast.core.dataset.DatasetModule;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetworkModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(int.class).toInstance(-1);
    bind(float.class).toInstance(-1F);
    bind(int[].class).toInstance(new int[0]);
    install(DatabaseModule.create());
    install(DatasetModule.create());
    install(NeuralNetworkModule.create());
    configureLog();
  }

  protected void configureLog() {

  }
}
