package de.lukasbreuer.cast.access.account.verification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.lukasbreuer.cast.access.account.AccountCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.security.Key;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class VerificationFactory {
  @Inject
  private final AccountCollection accountCollection;
  @Inject
  private final Key secret;

  public Verification create(String username, String password) {
    return Verification.create(accountCollection, secret, username, password);
  }
}
