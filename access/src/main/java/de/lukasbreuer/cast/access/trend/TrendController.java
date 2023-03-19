package de.lukasbreuer.cast.access.trend;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.symbol.SymbolProfile;
import de.lukasbreuer.cast.deploy.trend.TrendRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
      findStockInformation(trendingStocks.stream().filter(stock ->
        stock.matches("[a-zA-Z]+")).limit(10).toList())
        .thenAccept(completableFuture::complete));
    return completableFuture;
  }

  private CompletableFuture<Map<String, Object>> findStockInformation(
    List<String> trendingStocks
  ) {
    var futureResponse = new CompletableFuture<Map<String, Object>>();
    var stocks = Lists.<Map<String, String>>newArrayList();
    for (var stock : trendingStocks) {
      Symbol.createAndFetch(stock, 1, symbol -> symbol.profile(findApiKey(),
        profile -> processStockData(futureResponse, trendingStocks, stocks,
          symbol, profile)));
    }
    return futureResponse;
  }

  private void processStockData(
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
