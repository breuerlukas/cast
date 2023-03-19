package de.lukasbreuer.cast.core.symbol.request;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "create")
public final class FinanceQuoteRequest implements FinanceRequest<String> {
  private final String symbol;
  private final String apiKey;

  private static final String URL_FORMAT = "https://yfapi.net/v6/finance/" +
    "quote?symbols=%s";

  @Override
  public CompletableFuture<String> send() {
    var request = HttpRequest.newBuilder()
      .uri(URI.create(String.format(URL_FORMAT,
        URLEncoder.encode(symbol, StandardCharsets.UTF_8))))
      .header("X-API-KEY", apiKey).GET().build();
    return HttpClient.newHttpClient()
      .sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenApply(this::findSymbolName);
  }

  private String findSymbolName(String body) {
    var json = new JSONObject(body).getJSONObject("quoteResponse");
    if (json.isNull("result") || json.getJSONArray("result").isEmpty()) {
      return "-";
    }
    var result = json.getJSONArray("result").getJSONObject(0);
    if (result.has("longName")) {
      return result.getString("longName");
    }
    if (result.has("shortName")) {
      return result.getString("shortName");
    }
    return "-";
  }
}