package de.lukasbreuer.cast.core.yahoo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class YahooModule extends AbstractModule {
  @Provides
  @Singleton
  YahooConfiguration provideYahooConfiguration() throws Exception {
    return YahooConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  @Named("yahooApiKey")
  String provideYahooConfiguration(YahooConfiguration configuration) {
    return configuration.apiKey();
  }
}
