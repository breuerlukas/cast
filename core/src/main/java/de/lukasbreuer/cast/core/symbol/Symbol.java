package de.lukasbreuer.cast.core.symbol;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Symbol {
  public static Symbol createAndFetch(String name, int fetchPeriod) {
    var symbol = create(name, fetchPeriod);
    try {
      symbol.refreshHistory();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return symbol;
  }

  public static Symbol createAndLoad(String name) {
    var symbol = create(name, -1);
    try {
      symbol.loadData(Path.of("./src/main/resources/" + name + ".json"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return symbol;
  }

  @Getter
  private final String name;
  private final long fetchPeriod;
  private final Map<Long, HistoryEntry> history = Maps.newHashMap();

  public void refreshHistory() throws Exception {
    var data = fetchData();
    loadData(data);
  }

  private static final String DATA_FETCH_FORMAT = "https://query1.finance.yahoo.com/" +
    "v8/finance/chart/%s?range=%s&interval=%s";

  private String fetchData() throws Exception {
    var result = new StringBuilder();
    var url = new URL(String.format(DATA_FETCH_FORMAT, name, determineFetchPeriod(), "1d"));
    var connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      for (String line; (line = reader.readLine()) != null; ) {
        result.append(line);
      }
    }
    return result.toString();
  }

  private String determineFetchPeriod() {
    if (fetchPeriod < 0) {
      return "100y";
    }
    return fetchPeriod + "d";
  }

  private void safeData(String data) throws Exception {
    var writer = new FileWriter("./src/main/resources/" + name + ".json");
    var bufferedWriter = new BufferedWriter(writer);
    bufferedWriter.write(data);
    bufferedWriter.close();
    loadData(data);
  }

  private void loadData(Path filePath) throws Exception {
    loadData(Files.readString(Paths.get(filePath.toString()), StandardCharsets.US_ASCII));
  }

  private void loadData(String data) throws Exception {
    var json = new JSONObject(data).getJSONObject("chart").getJSONArray("result");
    var firstResult = json.getJSONObject(0);
    var timestamps = firstResult.getJSONArray("timestamp");
    var quotes = firstResult.getJSONObject("indicators").getJSONArray("quote").getJSONObject(0);
    var open = quotes.getJSONArray("open");
    var close = quotes.getJSONArray("close");
    var high = quotes.getJSONArray("high");
    var low = quotes.getJSONArray("low");
    var volume = quotes.getJSONArray("volume");
    for (var i = 0; i < timestamps.length(); i++) {
      if (timestamps.isNull(i) || open.isNull(i) || close.isNull(i) ||
        high.isNull(i) || low.isNull(i) || volume.isNull(i)
      ) {
        continue;
      }
      history.put(timestamps.getLong(i), HistoryEntry.create(this, timestamps.getLong(i), open.getDouble(i),
        close.getDouble(i), high.getDouble(i), low.getDouble(i), volume.getDouble(i)));
    }
  }

  public List<HistoryEntry> findPartOfHistory(int entryAmount) {
    if (entryAmount <= 0) {
      return Lists.newArrayList();
    }
    var totalHistory = findTotalHistory();
    return totalHistory.stream().skip(totalHistory.size() - entryAmount).collect(Collectors.toList());
  }

  public List<HistoryEntry> findTotalHistory() {
    return history.entrySet().stream()
      .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
      .map(Map.Entry::getValue).collect(Collectors.toList());
  }
}