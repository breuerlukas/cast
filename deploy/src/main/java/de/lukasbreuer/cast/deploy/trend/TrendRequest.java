package de.lukasbreuer.cast.deploy.trend;

import com.clearspring.analytics.util.Lists;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(staticName = "create")
public final class TrendRequest {
  private final String apiKey;

  private static final String URL_FORMAT = "https://yfapi.net/v1/finance/trending/US";

  public CompletableFuture<List<String>> send() {
    var request = HttpRequest.newBuilder()
      .uri(URI.create(String.format(URL_FORMAT)))
      .header("X-API-KEY", apiKey).GET().build();
    return HttpClient.newHttpClient()
      .sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenApply(this::findTrendingStocks);
  }

  private List<String>findTrendingStocks(String body) {
    var stocks = Lists.<String>newArrayList();
    var quotes = new JSONObject(body).getJSONObject("finance")
      .getJSONArray("result").getJSONObject(0).getJSONArray("quotes");
    for (var i = 0; i < quotes.length(); i++) {
      stocks.add(quotes.getJSONObject(i).getString("symbol"));
    }
    return stocks;
  }
}
