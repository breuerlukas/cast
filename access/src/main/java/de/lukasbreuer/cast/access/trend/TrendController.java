package de.lukasbreuer.cast.access.trend;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.symbol.SymbolProfile;
import de.lukasbreuer.cast.deploy.trend.TrendRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.nd4j.common.primitives.Atomic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrendController {
  @Qualifier("yahooApiKeys")
  private final List<String> yahooApiKeys;

  @RequestMapping(path = "/trends", method = RequestMethod.GET)
  public CompletableFuture<Map<String, Object>> findTrends() {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    TrendRequest.create(findApiKey()).send().thenAccept(trendingStocks ->
      findStockInformation(trendingStocks).thenAccept(completableFuture::complete));
    return completableFuture;
  }

  private CompletableFuture<Map<String, Object>> findStockInformation(
    List<String> trendingStocks
  ) {
    var symbolResponse = new CompletableFuture<List<Symbol>>();
    var symbols = Lists.<Symbol>newArrayList();
    for (var stock : trendingStocks) {
      Symbol.createAndFetch(stock, 1, symbol ->
        processSymbol(symbolResponse, trendingStocks, symbols, symbol));
    }
    var informationResponse = new CompletableFuture<Map<String, Object>>();
    symbolResponse.thenAccept(trendingSymbols ->
      findSymbolInformation(trendingStocks, trendingSymbols)
        .thenAccept(informationResponse::complete));
    return informationResponse;
  }

  private void processSymbol(
    CompletableFuture<List<Symbol>> futureResponse, List<String> trendingStocks,
    List<Symbol> symbols, Symbol symbol
  ) {
    symbols.add(symbol);
    if (symbols.size() == trendingStocks.size()) {
      futureResponse.complete(symbols);
    }
  }

  private CompletableFuture<Map<String, Object>> findSymbolInformation(
    List<String> trendingStocks, List<Symbol> symbols
  ) {
    var synchronizedTrendingStocks = Collections.synchronizedList(trendingStocks);
    var informationResponse = new CompletableFuture<Map<String, Object>>();
    var information = Lists.<Map<String, String>>newArrayList();
    var acceptedSymbols = 0;
    for (var symbol : symbols) {
      if (!symbol.name().matches("[a-zA-Z]+") || acceptedSymbols >= 10) {
        synchronizedTrendingStocks.remove(symbol.name());
        continue;
      }
      symbol.profile(findApiKey(), profile ->
        processProfile(informationResponse, synchronizedTrendingStocks,
          information, symbol, profile));
      acceptedSymbols += 1;
    }
    return informationResponse;
  }

  private void processProfile(
    CompletableFuture<Map<String, Object>> futureResponse, List<String> trendingStocks,
    List<Map<String, String>> stocks, Symbol symbol, SymbolProfile profile
  ) {
    if (profile.company().equals("-") && profile.industry().equals("-") && profile.website().equals("-")) {
      trendingStocks.remove(symbol.name());
    } else {
      stocks.add(transformStock(symbol.name(), profile.company(), profile.industry(),
        profile.website(), symbol.findPartOfHistory(1).get(0).close()));
    }
    if (stocks.size() == trendingStocks.size()) {
      var response = Maps.<String, Object>newHashMap();
      response.put("trends", stocks);
      futureResponse.complete(response);
    }
  }

  private Map<String, String> transformStock(
    String symbol, String company, String industry, String website, double price
  ) {
    var result = Maps.<String, String>newHashMap();
    result.put("symbol", symbol);
    result.put("company", company);
    result.put("industry", industry);
    result.put("website", website);
    result.put("price", String.valueOf(price));
    return result;
  }

  private String findApiKey() {
    return yahooApiKeys.get(ThreadLocalRandom.current().nextInt(0, yahooApiKeys.size()));
  }
}
