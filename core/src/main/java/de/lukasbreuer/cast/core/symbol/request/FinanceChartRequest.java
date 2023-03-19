package de.lukasbreuer.cast.core.symbol.request;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
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
public final class FinanceChartRequest implements FinanceRequest<Map<Long, HistoryEntry>> {
  private final String symbol;
  private final String range;
  private final String interval;

  private static final String URL_FORMAT = "https://query1.finance.yahoo.com/" +
    "v8/finance/chart/%s?range=%s&interval=%s";

  @Override
  public CompletableFuture<Map<Long, HistoryEntry>> send() {
    var request = HttpRequest.newBuilder()
      .uri(URI.create(String.format(URL_FORMAT,
        URLEncoder.encode(symbol, StandardCharsets.UTF_8), range, interval)))
      .GET().build();
    return HttpClient.newHttpClient()
      .sendAsync(request, HttpResponse.BodyHandlers.ofString())
      .thenApply(HttpResponse::body)
      .thenApply(this::createHistoryData);
  }

  private Map<Long, HistoryEntry> createHistoryData(String body) {
    var json = new JSONObject(body).getJSONObject("chart");
    if (json.isNull("result") || json.getJSONArray("result").isEmpty()) {
      return Maps.newHashMap();
    }
    var firstResult = json.getJSONArray("result").getJSONObject(0);
    if (!firstResult.has("timestamp")) {
      return Maps.newHashMap();
    }
    var timestamps = firstResult.getJSONArray("timestamp");
    var quotes = firstResult.getJSONObject("indicators").getJSONArray("quote").getJSONObject(0);
    var open = quotes.getJSONArray("open");
    var close = quotes.getJSONArray("close");
    var high = quotes.getJSONArray("high");
    var low = quotes.getJSONArray("low");
    var volume = quotes.getJSONArray("volume");
    return registerHistoryEntries(timestamps, open, close, high, low, volume);
  }

  private Map<Long, HistoryEntry> registerHistoryEntries(
    JSONArray timestamps, JSONArray open, JSONArray close, JSONArray high,
    JSONArray low, JSONArray volume
  ) {
    var history = Maps.<Long, HistoryEntry>newHashMap();
    for (var i = 0; i < timestamps.length(); i++) {
      if (timestamps.isNull(i) || open.isNull(i) || close.isNull(i) ||
        high.isNull(i) || low.isNull(i) || volume.isNull(i)
      ) {
        continue;
      }
      history.put(timestamps.getLong(i), HistoryEntry.create(timestamps.getLong(i),
        open.getDouble(i), close.getDouble(i), high.getDouble(i), low.getDouble(i),
        volume.getDouble(i)));
    }
    return history;
  }
}
