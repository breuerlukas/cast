package de.lukasbreuer.stockalgorithm.deploy.investopedia;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class InvestopediaModule extends AbstractModule {
  @Provides
  @Singleton
  InvestopediaConfiguration provideInvestopediaConfiguration() throws Exception {
    return InvestopediaConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  @Named("investopediaUsername")
  String provideInvestopediaUsername(InvestopediaConfiguration configuration) {
    return configuration.username();
  }

  @Provides
  @Singleton
  @Named("investopediaPassword")
  String provideInvestopediaPassword(InvestopediaConfiguration configuration) {
    return configuration.password();
  }

  @Provides
  @Singleton
  @Named("investopediaGame")
  String provideInvestopediaGame(InvestopediaConfiguration configuration) {
    return configuration.game();
  }
}
