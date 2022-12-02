package de.lukasbreuer.stockalgorithm.core.evaluation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.stockalgorithm.core.dataset.StockDataset;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.NeuralNetwork;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class EvaluationFactory {
  @Inject @Named("modelReviewPeriod")
  private final int reviewPeriod;
  @Inject @Named("modelDayLongestReview")
  private final int dayLongestReview;

  public Evaluation create(
    NeuralNetwork neuralNetwork, StockDataset evaluationDataset
  ) {
    return Evaluation.create(neuralNetwork, evaluationDataset,
      reviewPeriod, dayLongestReview);
  }
}
