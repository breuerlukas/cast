package de.lukasbreuer.cast.access;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @PostConstruct
  private void createModuleInjector() {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}
