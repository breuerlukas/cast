package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class StockAlgorithmModule extends AbstractModule {
  private static final int MODEL_SEED = 1;

  @Provides
  @Singleton
  @Named("modelSeed")
  int provideModelSeed() {
    return MODEL_SEED;
  }

  private static final int MODEL_TRAIN_PERIOD = 1;

  @Provides
  @Singleton
  @Named("modelTrainPeriod")
  int provideModelTrainPeriod() {
    return MODEL_TRAIN_PERIOD;
  }

  private static final int MODEL_TRAIN_MAXIMUM_TRADES = 1;

  @Provides
  @Singleton
  @Named("modelTrainMaximumTrades")
  int provideModelTrainMaximumTrades() {
    return MODEL_TRAIN_MAXIMUM_TRADES;
  }

  private static final int MODEL_EVALUATION_PERIOD = 1;

  @Provides
  @Singleton
  @Named("modelEvaluationPeriod")
  int provideModelEvaluationPeriod() {
    return MODEL_EVALUATION_PERIOD;
  }

  private static final int MODEL_EVALUATION_MAXIMUM_TRADES = 1;

  @Provides
  @Singleton
  @Named("modelEvaluationMaximumTrades")
  int provideModelEvaluationMaximumTrades() {
    return MODEL_EVALUATION_MAXIMUM_TRADES;
  }

  private static final int MODEL_REVIEW_PERIOD = 1;

  @Provides
  @Singleton
  @Named("modelReviewPeriod")
  int provideModelReviewPeriod() {
    return MODEL_REVIEW_PERIOD;
  }

  private static final int MODEL_BATCH_SIZE = 1;

  @Provides
  @Singleton
  @Named("modelBatchSize")
  int provideModelBatchSize() {
    return MODEL_BATCH_SIZE;
  }


  private static final int MODEL_TOTAL_BATCHES = 1;

  @Provides
  @Singleton
  @Named("modelTotalBatches")
  int provideModelTotalBatches() {
    return MODEL_TOTAL_BATCHES;
  }

}
