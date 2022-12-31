package de.lukasbreuer.cast.access.portfolio;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.deploy.portfolio.Stock;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PortfolioController {
  private final StockCollection stockCollection;

  @RequestMapping(path = "/portfolio/add", method = RequestMethod.POST)
  public void addStock(@RequestBody Map<String, Object> input) {
    stockCollection.addStock(Stock.create(UUID.randomUUID(),
      (String) input.get("stockName")), success -> {});
  }

  @RequestMapping(path = "/portfolio/update", method = RequestMethod.POST)
  public void updateStock(@RequestBody Map<String, Object> input) {
    stockCollection.updateStock(UUID.fromString((String) input.get("stockId")),
      (String) input.get("stockName"), success -> {});
  }

  @RequestMapping(path = "/portfolio/remove", method = RequestMethod.POST)
  public void removeStock(@RequestBody Map<String, Object> input) {
    stockCollection.removeStock(UUID.fromString((String) input.get("stockId")),
      success -> {});
  }

  @RequestMapping(path = "/portfolio", method = RequestMethod.GET)
  public CompletableFuture<Map<String, Object>> findPortfolio() {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    stockCollection.totalPortfolio(portfolio -> findPortfolio(completableFuture,
      portfolio));
    return completableFuture;
  }

  private void findPortfolio(
    CompletableFuture<Map<String, Object>> futureResponse, List<Stock> portfolio
  ) {
    var response = Maps.<String, Object>newHashMap();
    response.put("portfolio", portfolio.stream().map(this::transformStock).collect(Collectors.toList()));
    futureResponse.complete(response);
  }

  private Map<String, String> transformStock(Stock stock) {
    var result = Maps.<String, String>newHashMap();
    result.put("id", stock.id().toString());
    result.put("stock", stock.formattedStockName());
    return result;
  }
}
