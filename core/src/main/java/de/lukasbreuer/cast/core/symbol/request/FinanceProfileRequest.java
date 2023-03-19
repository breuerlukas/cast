package de.lukasbreuer.cast.core.symbol.request;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "create")
public final class FinanceProfileRequest implements FinanceRequest<Map<String, String>> {
  private final String symbol;
  private final String apiKey;

  private static final String URL_FORMAT = "https://yfapi.net/v11/finance/" +
    "quoteSummary/%s?modules=assetProfile";

  @Override
  public CompletableFuture<Map<String, String>> send() {
    var request = HttpRequest.newBuilder()
      .uri(URI.create(String.format(URL_FORMAT,
        URLEncoder.encode(symbol, StandardCharsets.UTF_8))))
      .header("X-API-KEY", apiKey).GET().build();
    return HttpClient.newHttpClient()
      .sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenApply(this::findProfileData);
  }

  private Map<String, String> findProfileData(String body) {
    var profileData = Maps.<String, String>newHashMap();
    profileData.put("industry", "-");
    profileData.put("website", "-");
    var json = new JSONObject(body).getJSONObject("quoteSummary");
    if (json.isNull("result") || json.getJSONArray("result").isEmpty()) {
      return profileData;
    }
    var result = json.getJSONArray("result").getJSONObject(0).getJSONObject("assetProfile");
    if (result.has("industry")) {
      profileData.put("industry", result.getString("industry"));
    }
    if (result.has("website")) {
      profileData.put("website", result.getString("website"));
    }
    return profileData;
  }
}
