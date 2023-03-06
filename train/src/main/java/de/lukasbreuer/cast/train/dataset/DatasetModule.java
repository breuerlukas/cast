package de.lukasbreuer.cast.train.dataset;

import com.clearspring.analytics.util.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.core.dataset.DatasetDay;
import de.lukasbreuer.cast.core.dataset.indicator.IndicatorRepository;
import de.lukasbreuer.cast.core.symbol.HistoryEntry;
import de.lukasbreuer.cast.core.symbol.Symbol;
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

  private static final int MODEL_EVALUATION_PERIOD = 365;

  @Provides
  @Singleton
  @Named("modelEvaluationPeriod")
  int provideModelEvaluationPeriod() {
    return MODEL_EVALUATION_PERIOD;
  }

  private static final int MODEL_BUY_REVIEW_PERIOD = 7 * 4;

  @Provides
  @Singleton
  @Named("modelBuyReviewPeriod")
  int provideBuyModelReviewPeriod() {
    return MODEL_BUY_REVIEW_PERIOD;
  }

  private static final int MODEL_SELL_REVIEW_PERIOD = 7 * 4;

  @Provides
  @Singleton
  @Named("modelSellReviewPeriod")
  int provideSellModelReviewPeriod() {
    return MODEL_SELL_REVIEW_PERIOD;
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

  private static final int MODEL_DAY_LONGEST_REVIEW = 16;

  @Provides
  @Singleton
  @Named("modelDayLongestReview")
  int provideDayLongestReview() {
    return MODEL_DAY_LONGEST_REVIEW;
  }

  @Provides
  @Singleton
  @Named("modelInputSizePerDay")
  int provideInputSizePerDay(@Named("modelDayLongestReview") int dayLongestReview) {
    var data = Lists.<HistoryEntry>newArrayList();
    var emptyEntry = HistoryEntry.create(Symbol.create("", 0), 0, 0, 0, 0, 0, 0);
    for (var i = 0; i < dayLongestReview + 1; i++) {
      data.add(emptyEntry);
    }
    var indicatorRepository = IndicatorRepository.create(data);
    indicatorRepository.fill();
    var datasetDay = DatasetDay.create(dayLongestReview, indicatorRepository);
    datasetDay.build();
    return datasetDay.raw().length;
  }
}
