package de.lukasbreuer.cast.access.account.verification;

import de.lukasbreuer.cast.access.account.AccountCollection;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.util.Date;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "create")
public final class Verification {
  private final AccountCollection accountCollection;
  private final Key secret;
  private final String username;
  private final String password;

  public void isAuthenticated(Consumer<Boolean> authenticated) {
    if (username == null || password == null || username.equals("") || password.equals("")) {
      authenticated.accept(false);
      return;
    }
    accountCollection.accountExists(username, exists ->
      isAuthenticated(authenticated, exists));
  }

  private void isAuthenticated(Consumer<Boolean> authenticated, boolean exists) {
    if (!exists) {
      authenticated.accept(false);
      return;
    }
    accountCollection.findAccountByName(username, account ->
      authenticated.accept(account.password().equals(password)));
  }

  private static final int EXPIRATION_TIME = 1000 * 60 * 60;

  public String generateApiKey() {
    var expiration = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    return Jwts.builder()
      .setExpiration(expiration)
      .claim("username", username)
      .signWith(secret)
      .compact();
  }
}
