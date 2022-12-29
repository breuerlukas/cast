package de.lukasbreuer.cast.access;

import com.google.inject.Guice;
import de.lukasbreuer.cast.access.account.AccountModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessConfiguration {
  @PostConstruct
  private void createModuleInjector() {
    var injector = Guice.createInjector(AccountModule.create());
    injector.injectMembers(this);
  }
}
