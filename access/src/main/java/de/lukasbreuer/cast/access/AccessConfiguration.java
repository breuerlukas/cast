package de.lukasbreuer.cast.access;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessConfiguration {
  @Inject
  private Log log;
  @Inject
  private ModelCollection modelCollection;

  @Bean
  Log provideLog() {
    return log;
  }

  @Bean
  ModelCollection provideModelCollection() {
    return modelCollection;
  }

  @PostConstruct
  private void createModuleInjector() {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}
