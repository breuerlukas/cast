package de.lukasbreuer.cast.deploy.trade;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class TradeModule extends AbstractModule {
  @Provides
  @Singleton
  TradeCollection provideTradeCollection(DatabaseConnection databaseConnection) {
    return TradeCollection.create(databaseConnection);
  }
}
