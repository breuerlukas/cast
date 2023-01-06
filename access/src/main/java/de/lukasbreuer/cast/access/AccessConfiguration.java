package de.lukasbreuer.cast.access;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.access.account.verification.VerificationFactory;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class AccessConfiguration {
  @Inject
  private Log log;
  @Inject
  private StockCollection stockCollection;
  @Inject
  private ModelCollection modelCollection;
  @Inject
  private TradeCollection tradeCollection;
  @Inject
  private Key secretKey;
  @Inject
  private VerificationFactory verificationFactory;

  @Bean
  Log provideLog() {
    return log;
  }

  @Bean
  StockCollection provideStockCollection() {
    return stockCollection;
  }

  @Bean
  ModelCollection provideModelCollection() {
    return modelCollection;
  }

  @Bean
  TradeCollection provideTradeCollection() {
    return tradeCollection;
  }

  @Bean
  Key provideSecretKey() {
    return secretKey;
  }

  @Bean
  VerificationFactory provideVerificationFactory() {
    return verificationFactory;
  }

  @PostConstruct
  private void createModuleInjector() {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}
