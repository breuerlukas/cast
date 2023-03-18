package de.lukasbreuer.cast.access.stock;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import de.lukasbreuer.cast.core.symbol.Symbol;
import de.lukasbreuer.cast.core.trade.TradeType;
import de.lukasbreuer.cast.deploy.model.Model;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.trade.Trade;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StockController {
  private final ModelCollection modelCollection;
  private final TradeCollection tradeCollection;

  @RequestMapping(path = "/stock/prices", method = RequestMethod.POST)
  public CompletableFuture<Map<String, Object>> findPrices(
    @RequestBody Map<String, Object> input
  ) {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    var reviewPeriod = Integer.parseInt((String) input.get("reviewPeriod"));
    Symbol.createAndFetch((String) input.get("stock"), reviewPeriod, symbol ->
      findPrices(completableFuture, symbol.findTotalHistory()));
    return completableFuture;
  }

  private void findPrices(
    CompletableFuture<Map<String, Object>> futureResponse, List<HistoryEntry> data
  ) {
    var response = Maps.<String, Object>newHashMap();
    response.put("prices", data.stream().map(HistoryEntry::close)
      .collect(Collectors.toList()));
    futureResponse.complete(response);
  }

  @RequestMapping(path = "/stock/predictions", method = RequestMethod.POST)
  public CompletableFuture<Map<String, Object>> findPredictions(
    @RequestBody Map<String, Object> input, HttpServletResponse servletResponse
  ) {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    var stock = ((String) input.get("stock")).toUpperCase();
    modelCollection.modelExists(stock, exists ->
      findPredictions(servletResponse, completableFuture,
        stock, Integer.parseInt((String) input.get("reviewPeriod")), exists));
    return completableFuture;
  }

  private void findPredictions(
    HttpServletResponse servletResponse,
    CompletableFuture<Map<String, Object>> futureResponse, String stock,
    int reviewPeriod, boolean exists
  ) {
    if (!exists) {
      servletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
      futureResponse.complete(Maps.newHashMap());
      return;
    }
    modelCollection.findByStock(stock, model ->
      tradeCollection.findLatestByStock(stock, TradeType.BUY, latestBuy ->
        tradeCollection.findLatestByStock(stock, TradeType.SELL, latestSell ->
          model.initialize(() -> findPredictions(futureResponse, model, reviewPeriod,
            latestBuy, latestSell)))));
  }

  private void findPredictions(
    CompletableFuture<Map<String, Object>> futureResponse, Model model,
    int reviewPeriod, Optional<Trade> latestBuy, Optional<Trade> latestSell
  ) {
    var tradeType = TradeType.BUY;
    if ((latestBuy.isPresent() && latestSell.isEmpty()) ||
      (latestBuy.isPresent() && latestSell.isPresent() &&
        latestBuy.get().tradeTime() > latestSell.get().tradeTime())
    ) {
      tradeType = TradeType.SELL;
    }
    var response = Maps.<String, Object>newHashMap();
    response.put("predictions", model.predict(tradeType, reviewPeriod));
    futureResponse.complete(response);
  }
}
