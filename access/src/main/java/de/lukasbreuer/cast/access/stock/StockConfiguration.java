package de.lukasbreuer.cast.access.stock;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.access.AccessModule;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockConfiguration {
  @Inject
  private ModelCollection modelCollection;

  @Bean
  ModelCollection provideModelCollection() {
    return modelCollection;
  }

  @PostConstruct
  private void createModuleInjector() throws Exception {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}