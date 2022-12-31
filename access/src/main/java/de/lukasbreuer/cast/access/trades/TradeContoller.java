package de.lukasbreuer.cast.access.trades;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.deploy.trade.Trade;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TradeContoller {
  private final TradeCollection tradeCollection;

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
