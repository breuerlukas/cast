package de.lukasbreuer.stockalgorithm.train.dataset;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class DatasetModule extends AbstractModule {
  private static final int MODEL_TRAIN_PERIOD = 365 * 4;

  @Provides
  @Singleton
  @Named("modelTrainPeriod")
  int provideModelTrainPeriod() {
    return MODEL_TRAIN_PERIOD;
  }

  private static final int MODEL_TRAIN_MAXIMUM_TRADES = 8;

  @Provides
  @Singleton
  @Named("modelTrainMaximumTrades")
  int provideModelTrainMaximumTrades() {
    return MODEL_TRAIN_MAXIMUM_TRADES;
  }

  private static final int MODEL_EVALUATION_PERIOD = 365;

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

  private static final int MODEL_REVIEW_PERIOD = 28;

  @Provides
  @Singleton
  @Named("modelReviewPeriod")
  int provideModelReviewPeriod() {
    return MODEL_REVIEW_PERIOD;
  }

  private static final int MODEL_BATCH_SIZE = 10;

  @Provides
  @Singleton
  @Named("modelBatchSize")
  int provideModelBatchSize() {
    return MODEL_BATCH_SIZE;
  }

  private static final int MODEL_TOTAL_BATCHES = MODEL_TRAIN_PERIOD;

  @Provides
  @Singleton
  @Named("modelTotalBatches")
  int provideModelTotalBatches() {
    return MODEL_TOTAL_BATCHES;
  }

  private static final int MODEL_TRADE_GENERALISATION_STEP_SIZE = 9;

  @Provides
  @Singleton
  @Named("modelTradeGeneralisationStepSize")
  int provideModelTradeGeneralisationStepSize() {
    return MODEL_TRADE_GENERALISATION_STEP_SIZE;
  }

  private static final int MODEL_NOISE_REMOVAL_STEP_SIZE = 9;

  @Provides
  @Singleton
  @Named("modelNoiseRemovalStepSize")
  int provideModelNoiseRemovalStepSize() {
    return MODEL_NOISE_REMOVAL_STEP_SIZE;
  }

  private static final int MODEL_INPUT_SIZE_PER_DAY = 42;

  @Provides
  @Singleton
  @Named("modelInputSizePerDay")
  int provideInputSizePerDay() {
    return MODEL_INPUT_SIZE_PER_DAY;
  }

  private static final int MODEL_DAY_LONGEST_REVIEW = 21;

  @Provides
  @Singleton
  @Named("modelDayLongestReview")
  int provideDayLongestReview() {
    return MODEL_DAY_LONGEST_REVIEW;
  }
}
