package de.lukasbreuer.cast.access;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.access.account.verification.VerificationFactory;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.notification.DeviceCollection;
import de.lukasbreuer.cast.deploy.notification.NotificationConfiguration;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.List;

@Configuration
public class AccessConfiguration {
  @Inject
  private Log log;
  @Inject @Named ("yahooApiKeys")
  private List<String> yahooApiKeys;
  @Inject @Named ("modelInputSizePerDay")
  private int inputSizePerDay;
  @Inject @Named ("modelDayLongestReview")
  private int dayLongestReview;
  @Inject
  private StockCollection stockCollection;
  @Inject
  private ModelCollection modelCollection;
  @Inject
  private TradeCollection tradeCollection;
  @Inject
  private DeviceCollection deviceCollection;
  @Inject
  private NotificationConfiguration notificationConfiguration;
  @Inject
  private Key secretKey;
  @Inject
  private VerificationFactory verificationFactory;

  @Bean
  Log provideLog() {
    return log;
  }

  @Bean
  @Qualifier("yahooApiKeys")
  List<String> yahooApiKey() {
    return yahooApiKeys;
  }

  @Bean
  @Qualifier("modelInputSizePerDay")
  int inputSizePerDay() {
    return inputSizePerDay;
  }

  @Bean
  @Qualifier("modelDayLongestReview")
  int dayLongestReview() {
    return dayLongestReview;
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
  DeviceCollection provideDeviceCollection() {
    return deviceCollection;
  }

  @Bean
  NotificationConfiguration provideNotificationConfiguration() {
    return notificationConfiguration;
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
