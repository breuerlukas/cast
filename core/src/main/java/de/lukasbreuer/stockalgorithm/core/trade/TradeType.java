package de.lukasbreuer.stockalgorithm.core.trade;

public enum TradeType {
  BUY,
  SELL;

  public boolean isBuy() {
    return this == BUY;
  }

  public boolean isSell() {
    return this == SELL;
  }
}
