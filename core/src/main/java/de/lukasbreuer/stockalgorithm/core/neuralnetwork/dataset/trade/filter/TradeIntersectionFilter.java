package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.trade.filter;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.trade.Trade;

import java.util.List;

public final class TradeIntersectionFilter extends TradeFilter {
  public static TradeIntersectionFilter create(List<Trade> initialTrades) {
    return new TradeIntersectionFilter(initialTrades);
  }

  private TradeIntersectionFilter(List<Trade> initialTrades) {
    super(initialTrades);
  }

  @Override
  public List<Trade> filter() {
    var qualifiedTrades = Lists.<Trade>newArrayList();
    var excludedTrades = Lists.<Trade>newArrayList();
    for (var i = 0; i < initialTrades().size(); i++) {
      var trade = initialTrades().get(i);
      if (excludedTrades.contains(trade)) {
        continue;
      }
      qualifiedTrades.add(trade);
      crosscheckTrade(trade, qualifiedTrades, excludedTrades);
    }
    return qualifiedTrades;
  }

  private void crosscheckTrade(
    Trade trade, List<Trade> qualifiedTrades, List<Trade> excludedTrades
  ) {
    for (var j = 0; j < initialTrades().size(); j++) {
      var reviewedTrade = initialTrades().get(j);
      if (reviewedTrade.equals(trade) || excludedTrades.contains(reviewedTrade)) {
        continue;
      }
      if (isTradeIntersectionPresent(trade, reviewedTrade)) {
        excludedTrades.add(reviewedTrade);
        qualifiedTrades.remove(reviewedTrade);
      }
    }
  }

  private boolean isTradeIntersectionPresent(Trade trade, Trade reviewedTrade) {
    if (isEnclosedIn(trade, reviewedTrade)) {
      return true;
    }
    if (allowsEarlierBuyTime(trade, reviewedTrade)) {
      trade.buyTime(reviewedTrade.buyTime());
      return true;
    }
    if (allowsLaterSellTime(trade, reviewedTrade)) {
      trade.sellTime(reviewedTrade.sellTime());
      return true;
    }
    return false;
  }

  private boolean isEnclosedIn(Trade trade, Trade reviewedTrade) {
    return reviewedTrade.buyTime() >= trade.buyTime() &&
      reviewedTrade.sellTime() <= trade.sellTime();
  }

  private boolean allowsEarlierBuyTime(Trade trade, Trade reviewedTrade) {
    return reviewedTrade.sellTime() >= trade.buyTime() &&
      reviewedTrade.sellTime() <= trade.sellTime() &&
      reviewedTrade.buyTime() <= trade.buyTime();
  }

  private boolean allowsLaterSellTime(Trade trade, Trade reviewedTrade) {
    return reviewedTrade.buyTime() >= trade.buyTime() &&
      reviewedTrade.buyTime() <= trade.sellTime() &&
      reviewedTrade.sellTime() >= trade.sellTime();
  }
}
