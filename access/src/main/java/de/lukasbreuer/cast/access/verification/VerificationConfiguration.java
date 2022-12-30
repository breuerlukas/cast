package de.lukasbreuer.cast.access.verification;

import com.google.inject.Guice;
import com.google.inject.Inject;
import de.lukasbreuer.cast.access.AccessModule;
import de.lukasbreuer.cast.access.account.verification.VerificationFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class VerificationConfiguration {
  @Inject
  private Key secretKey;
  @Inject
  private VerificationFactory verificationFactory;

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
