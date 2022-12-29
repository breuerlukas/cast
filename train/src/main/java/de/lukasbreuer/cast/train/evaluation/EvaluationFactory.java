package de.lukasbreuer.cast.train.evaluation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetwork;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class EvaluationFactory {
  @Inject @Named("modelReviewPeriod")
  private final int reviewPeriod;
  @Inject @Named("modelDayLongestReview")
  private final int dayLongestReview;
  @Inject @Named("modelEvaluationMaximumTrades")
  private final int evaluationMaximumTrades;

  public Evaluation create(
    NeuralNetwork neuralNetwork, StockDataset evaluationDataset
  ) {
    return Evaluation.create(neuralNetwork, evaluationDataset,
      reviewPeriod, dayLongestReview, evaluationMaximumTrades);
  }
}
