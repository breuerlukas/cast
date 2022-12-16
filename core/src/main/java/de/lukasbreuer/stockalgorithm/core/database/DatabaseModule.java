package de.lukasbreuer.stockalgorithm.core.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class DatabaseModule extends AbstractModule {
  @Provides
  @Singleton
  DatabaseConfiguration provideDatabaseConfiguration() throws Exception {
    return DatabaseConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  DatabaseConnection provideDatabaseConnection(DatabaseConfiguration configuration) {
    var connection = DatabaseConnection.create("localhost", 27017,
      configuration.databaseName());
    connection.connect();
    return connection;
  }
}
