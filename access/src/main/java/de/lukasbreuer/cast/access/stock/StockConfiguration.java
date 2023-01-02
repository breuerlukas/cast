package de.lukasbreuer.cast.access.stock;

import com.google.inject.Guice;
import de.lukasbreuer.cast.access.AccessModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockConfiguration {
  @PostConstruct
  private void createModuleInjector() throws Exception {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}