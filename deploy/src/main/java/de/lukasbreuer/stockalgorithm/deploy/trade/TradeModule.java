package de.lukasbreuer.stockalgorithm.deploy.trade;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class TradeModule extends AbstractModule {
  @Provides
  @Singleton
  TradeCollection provideTradeCollection(DatabaseConnection databaseConnection) {
    return TradeCollection.create(databaseConnection);
  }
}
