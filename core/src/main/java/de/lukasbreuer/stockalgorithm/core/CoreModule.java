package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseConfiguration;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseModule;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class CoreModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(int.class).toInstance(-1);
    bind(float.class).toInstance(-1F);
    bind(int[].class).toInstance(new int[0]);
    install(DatabaseModule.create());
  }

  @Provides
  @Singleton
  @Named("coreLog")
  Log provideCoreLog() {
    return Log.create("Core");
  }
}
