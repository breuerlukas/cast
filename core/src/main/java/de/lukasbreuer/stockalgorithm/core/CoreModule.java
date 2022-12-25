package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.AbstractModule;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseModule;
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
}
