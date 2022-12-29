package de.lukasbreuer.cast.deploy.model;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class ModelModule extends AbstractModule {
  @Provides
  @Singleton
  ModelCollection provideModelCollection(
    DatabaseConnection databaseConnection, ModelFactory modelFactory
  ) {
    return ModelCollection.create(databaseConnection,  modelFactory);
  }
}
