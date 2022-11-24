package de.lukasbreuer.stockalgorithm.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "create")
public final class Trade {
  private int buyTime;
  private int sellTime;

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