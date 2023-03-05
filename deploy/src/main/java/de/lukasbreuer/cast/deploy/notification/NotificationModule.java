package de.lukasbreuer.cast.deploy.notification;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class NotificationModule extends AbstractModule {
  @Provides
  @Singleton
  NotificationConfiguration provideNotificationConfiguration() throws Exception {
    return NotificationConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  @Named("notificationServerKey")
  String provideInvestopediaUsername(NotificationConfiguration configuration) {
    return configuration.severKey();
  }

  @Provides
  @Singleton
  DeviceCollection provideDeviceCollection(DatabaseConnection databaseConnection) {
    return DeviceCollection.create(databaseConnection);
  }
}
