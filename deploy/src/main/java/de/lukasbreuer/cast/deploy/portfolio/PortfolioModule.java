package de.lukasbreuer.cast.deploy.portfolio;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class PortfolioModule extends AbstractModule {
  @Provides
  @Singleton
  StockCollection provideStockCollection(DatabaseConnection databaseConnection) {
    return StockCollection.create(databaseConnection);
  }
}
