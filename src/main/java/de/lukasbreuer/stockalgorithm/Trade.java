package de.lukasbreuer.stockalgorithm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Trade {
  private final int buyTime;
  private final int sellTime;

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Trade trade)) {
      return false;
    }
    return trade.buyTime() == buyTime && trade.sellTime() == sellTime;
  }

  @Override
  public String toString() {
    return "Trade {" +
      "buyTime=" + buyTime +
      ", sellTime=" + sellTime +
      '}';
  }
}