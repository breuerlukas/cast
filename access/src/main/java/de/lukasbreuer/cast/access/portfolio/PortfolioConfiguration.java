package de.lukasbreuer.cast.access.portfolio;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.access.AccessModule;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortfolioConfiguration {
  @Inject
  private StockCollection stockCollection;

  @Bean
  StockCollection provideStockCollection() {
    return stockCollection;
  }

  @PostConstruct
  private void createModuleInjector() throws Exception {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}