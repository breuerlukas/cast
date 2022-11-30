package de.lukasbreuer.stockalgorithm.core.dataset;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class StockDatasetFactory {
  @Inject
  @Named("modelSeed")
  private final int seed;

  public StockDataset create() {
    System.out.println(seed);
    return null;
  }
}
