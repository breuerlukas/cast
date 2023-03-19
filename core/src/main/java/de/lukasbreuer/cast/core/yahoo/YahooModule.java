package de.lukasbreuer.cast.core.yahoo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(staticName = "create")
public final class YahooModule extends AbstractModule {
  @Provides
  @Singleton
  YahooConfiguration provideYahooConfiguration() throws Exception {
    return YahooConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  @Named("yahooApiKeys")
  List<String> provideYahooConfiguration(YahooConfiguration configuration) {
    return configuration.apiKeys();
  }
}
