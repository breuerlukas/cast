package de.lukasbreuer.stockalgorithm.core.dataset;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.ModelState;
import de.lukasbreuer.stockalgorithm.core.symbol.Symbol;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class StockDatasetFactory {
  @Inject @Named("modelTrainPeriod")
  private final int trainPeriod;
  @Inject @Named("modelTrainMaximumTrades")
  private final int trainMaximumTrades;
  @Inject @Named("modelEvaluationPeriod")
  private final int evaluationPeriod;
  @Inject @Named("modelEvaluationMaximumTrades")
  private final int evaluationMaximumTrades;
  @Inject @Named("modelReviewPeriod")
  private final int reviewPeriod;
  @Inject @Named("modelBatchSize")
  private final int batchSize;
  @Inject @Named("modelTotalBatches")
  private final int totalBatches;
  @Inject @Named("modelTrainGeneralisationStepSize")
  private final int tradeGeneralisationStepSize;
  @Inject @Named("modelNoiseRemovalStepSize")
  private final int tradeNoiseRemovalStepSize;
  @Inject @Named("modelDayLongestReview")
  private final int dayLongestReview;

  public StockDataset createAndBuild(
    Symbol symbol, TradeType tradeType, ModelState modelState, int seed
  ) {
    var dataset = create(symbol, tradeType, modelState, seed);
    dataset.build();
    return dataset;
  }

  public StockDataset create(
    Symbol symbol, TradeType tradeType, ModelState modelState, int seed
  ) {
    return StockDataset.create(symbol, tradeType, modelState, seed, trainPeriod,
      trainMaximumTrades, evaluationPeriod, evaluationMaximumTrades, reviewPeriod,
      batchSize, totalBatches, tradeGeneralisationStepSize, tradeNoiseRemovalStepSize,
      dayLongestReview);
  }
}
