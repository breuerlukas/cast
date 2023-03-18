package de.lukasbreuer.cast.core.symbol;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.symbol.request.FinanceChartRequest;
import de.lukasbreuer.cast.core.symbol.request.FinanceProfileRequest;
import de.lukasbreuer.cast.core.symbol.request.FinanceQuoteRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Symbol {
  public static void createAndFetch(
    String name, int fetchPeriod, Consumer<Symbol> futureSymbol
  ) {
    var symbol = create(name, fetchPeriod);
    try {
      symbol.refreshHistory(() -> futureSymbol.accept(symbol));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static Symbol createAndFetch(String name, int fetchPeriod, Runnable completed) {
    var symbol = create(name, fetchPeriod);
    try {
      symbol.refreshHistory(completed);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return symbol;
  }

  @Getter
  private final String name;
  private final long fetchPeriod;
  private Map<Long, HistoryEntry> history = Maps.newHashMap();

  public void refreshHistory(Runnable completed) {
    FinanceChartRequest.create(name, determineFetchPeriod(), "1d").send()
      .thenAccept(chart -> history = chart).thenAccept(empty -> completed.run());
  }

  private String determineFetchPeriod() {
    if (fetchPeriod < 0) {
      return "100y";
    }
    return fetchPeriod + "d";
  }

  public void profile(String apiKey, Consumer<SymbolProfile> futureProfile) {
    FinanceProfileRequest.create(name, apiKey).send()
      .thenAccept(profileData -> FinanceQuoteRequest.create(name, apiKey).send()
        .thenAccept(company -> futureProfile.accept(SymbolProfile.create(name,
          company, profileData.get("industry"), profileData.get("website")))));
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
      .sorted(Map.Entry.comparingByKey())
      .map(Map.Entry::getValue).collect(Collectors.toList());
  }
}