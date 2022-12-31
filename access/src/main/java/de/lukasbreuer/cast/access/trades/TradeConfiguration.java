package de.lukasbreuer.cast.access.trades;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.access.AccessModule;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TradeConfiguration {
  @Inject
  private TradeCollection tradeCollection;

  @Bean
  TradeCollection provideTradeCollection() {
    return tradeCollection;
  }

  @PostConstruct
  private void createModuleInjector() throws Exception {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}