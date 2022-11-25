package de.lukasbreuer.stockalgorithm.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class StockAlgorithmModule extends AbstractModule {
  private static final int MODEL_TRAIN_PERIOD = 1;

  @Provides
  @Singleton
  @Named("modelTrainPeriod")
  int provideModelTrainPeriod() {
    return MODEL_TRAIN_PERIOD;
  }

  private static final int MODEL_EVALUATION_PERIOD = 1;

  @Provides
  @Singleton
  @Named("modelEvaluationPeriod")
  int provideModelEvaluationPeriod() {
    return MODEL_EVALUATION_PERIOD;
  }


  private static final int MODEL_REVIEW_PERIOD = 1;

  @Provides
  @Singleton
  @Named("modelReviewPeriod")
  int provideModelReviewPeriod() {
    return MODEL_REVIEW_PERIOD;
  }
}
