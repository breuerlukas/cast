package de.lukasbreuer.cast.access.account;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.access.account.verification.VerificationConfiguration;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@RequiredArgsConstructor(staticName = "create")
public final class AccountModule extends AbstractModule {
  @Provides
  @Singleton
  AccountCollection provideAccountCollection(DatabaseConnection databaseConnection) {
    return AccountCollection.create(databaseConnection);
  }

  @Provides
  @Singleton
  VerificationConfiguration provideVerificationConfiguration() throws Exception {
    return VerificationConfiguration.createAndLoad();
  }

  @Provides
  @Singleton
  Key provideSecretVerificationKey(VerificationConfiguration configuration) {
    return new SecretKeySpec(configuration.verificationSecret()
      .getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
  }
}
