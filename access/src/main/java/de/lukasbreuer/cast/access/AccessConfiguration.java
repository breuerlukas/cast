package de.lukasbreuer.cast.access;

import com.google.inject.Guice;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessConfiguration {
  @PostConstruct
  private void createModuleInjector() {
    var injector = Guice.createInjector(AccessModule.create());
    injector.injectMembers(this);
  }
}
