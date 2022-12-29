package de.lukasbreuer.cast.access.verification;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.access.account.verification.Verification;
import de.lukasbreuer.cast.access.account.verification.VerificationFactory;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class VerificationController {
  private final Key secretKey;
  private final VerificationFactory verificationFactory;

  @RequestMapping(path = "/verification/login", method = RequestMethod.POST)
  public CompletableFuture<Map<String, Object>> login(
    @RequestBody Map<String, Object> input, HttpServletResponse servletResponse
  ) {
    var verification = verificationFactory.create(((String) input.get("username")).toLowerCase(),
      (String) input.get("password"));
    var futureResponse = new CompletableFuture<Map<String, Object>>();
    verification.isAuthenticated(isAuthenticated ->
      completeLogin(servletResponse, verification, futureResponse, isAuthenticated));
    return futureResponse;
  }

  private void completeLogin(
    HttpServletResponse servletResponse, Verification verification,
    CompletableFuture<Map<String, Object>> futureResponse, boolean isAuthenticated
  ) {
    if (isAuthenticated) {
      var response = Maps.<String, Object>newHashMap();
      response.put("apiKey", verification.generateApiKey());
      futureResponse.complete(response);
    } else {
      servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      futureResponse.complete(Maps.newHashMap());
    }
  }

  @RequestMapping(path = "/verification/isValid", method = RequestMethod.POST)
  public Map<String, Object> isValid(@RequestBody Map<String, Object> input) {
    var response = Maps.<String, Object>newHashMap();
    try {
      Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws((String) input.get("token"));
      response.put("isValid", "true");
    } catch (Exception exception) {
      response.put("isValid", "false");
    }
    return response;
  }
}
