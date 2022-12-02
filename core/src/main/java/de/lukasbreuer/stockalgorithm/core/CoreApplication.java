package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.Guice;

public final class CoreApplication {
  public static void main(String[] args) {
    var injector = Guice.createInjector(StockAlgorithmModule.create());

  }
}
