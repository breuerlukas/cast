package de.lukasbreuer.cast.core.dataset.trade.filter;

import de.lukasbreuer.cast.core.trade.Trade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TradeFilter {
  @Getter(AccessLevel.PROTECTED)
  private final List<Trade> initialTrades;

  public abstract List<Trade> filter();
}
