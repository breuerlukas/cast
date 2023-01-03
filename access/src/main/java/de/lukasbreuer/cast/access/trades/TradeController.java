package de.lukasbreuer.cast.access.trades;

import com.clearspring.analytics.util.Lists;
import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.TradeType;
import de.lukasbreuer.cast.deploy.portfolio.Stock;
import de.lukasbreuer.cast.deploy.portfolio.StockCollection;
import de.lukasbreuer.cast.deploy.trade.Trade;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TradeController {
  private final StockCollection stockCollection;
  private final TradeCollection tradeCollection;

  @RequestMapping(path = "/trades/current", method = RequestMethod.GET)
  public CompletableFuture<Map<String, Object>> findCurrentTrades() {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    stockCollection.totalPortfolio(portfolio -> findLatestTrades(portfolio,
      latestTrades -> findCurrentTrades(completableFuture, latestTrades)));
    return completableFuture;
  }

  private void findLatestTrades(
    List<Stock> portfolio,
    Consumer<List<Map.Entry<Optional<Trade>, Optional<Trade>>>> futureTrades
  ) {
    var latestTrades = Lists.<Map.Entry<Optional<Trade>, Optional<Trade>>>newArrayList();
    for (var stock : portfolio) {
      tradeCollection.findLatestByStock(stock.stockName(), TradeType.BUY, latestBuy ->
        tradeCollection.findLatestByStock(stock.stockName(), TradeType.SELL, latestSell ->
            processStockLatestTrades(futureTrades, latestTrades, portfolio.size(),
              latestBuy, latestSell)));
    }
  }

  private void processStockLatestTrades(
    Consumer<List<Map.Entry<Optional<Trade>, Optional<Trade>>>> futureTrades,
    List<Map.Entry<Optional<Trade>, Optional<Trade>>> totalLatestTrades,
    int totalStocks, Optional<Trade> latestBuy, Optional<Trade> latestSell
  ) {
    totalLatestTrades.add(new AbstractMap.SimpleEntry<>(latestBuy, latestSell));
    if (totalLatestTrades.size() == totalStocks) {
      futureTrades.accept(totalLatestTrades);
    }
  }

  private void findCurrentTrades(
    CompletableFuture<Map<String, Object>> futureResponse,
    List<Map.Entry<Optional<Trade>, Optional<Trade>>> latestTrades
  ) {
    var currentTrades = Lists.<Trade>newArrayList();
    for (var entry : latestTrades) {
      var latestBuy = entry.getKey();
      var latestSell = entry.getValue();
      if ((latestBuy.isPresent() && latestSell.isEmpty()) ||
        (latestBuy.isPresent() && latestSell.isPresent() &&
          latestBuy.get().tradeTime() > latestSell.get().tradeTime())
      ) {
        currentTrades.add(latestBuy.get());
      }
    }
    var response = Maps.<String, Object>newHashMap();
    response.put("trades", currentTrades.stream()
      .map(trade -> transformCurrentTrade(trade, currentStockPrice(trade.stock())))
      .collect(Collectors.toList()));
    futureResponse.complete(response);
  }

  private double currentStockPrice(String stock) {
    return Symbol.createAndFetch(stock, 1).findPartOfHistory(1).get(0).close();
  }

  private Map<String, String> transformCurrentTrade(Trade trade, double currentPrice) {
    var regularTransformation = transformTrade(trade);
    regularTransformation.remove("tradeType");
    regularTransformation.put("currentPrice", String.valueOf(currentPrice));
    return regularTransformation;
  }

  @RequestMapping(path = "/trades", method = RequestMethod.POST)
  public CompletableFuture<Map<String, Object>> findTrades(
    @RequestBody Map<String, Object> input
  ) {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    tradeCollection.findByStock((String) input.get("stock"),
      trades -> findTrades(completableFuture, trades));
    return completableFuture;
  }

  private void findTrades(
    CompletableFuture<Map<String, Object>> futureResponse, List<Trade> trades
  ) {
    var response = Maps.<String, Object>newHashMap();
    response.put("trades", trades.stream().map(this::transformTrade).collect(Collectors.toList()));
    futureResponse.complete(response);
  }

  private Map<String, String> transformTrade(Trade trade) {
    var result = Maps.<String, String>newHashMap();
    result.put("id", trade.id().toString());
    result.put("stock", trade.stock());
    result.put("tradeType", trade.tradeType().toString());
    result.put("tradeTime", String.valueOf(trade.tradeTime()));
    result.put("tradePrice", String.valueOf(trade.tradePrice()));
    return result;
  }
}
