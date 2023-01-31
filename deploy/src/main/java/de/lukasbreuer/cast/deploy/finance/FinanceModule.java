package de.lukasbreuer.cast.deploy.finance;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class FinanceModule extends AbstractModule {
  @Provides
  @Singleton
  BankAccountCollection provideBankAccountCollection(DatabaseConnection databaseConnection) {
    return BankAccountCollection.create(databaseConnection);
  }
}
