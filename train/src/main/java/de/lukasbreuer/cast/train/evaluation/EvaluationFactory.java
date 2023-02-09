package de.lukasbreuer.cast.train.evaluation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.core.dataset.StockDataset;
import de.lukasbreuer.cast.core.neuralnetwork.NeuralNetwork;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class EvaluationFactory {
  @Inject @Named("modelBuyReviewPeriod")
  private final int buyReviewPeriod;
  @Inject @Named("modelSellReviewPeriod")
  private final int sellReviewPeriod;
  @Inject @Named("modelDayLongestReview")
  private final int dayLongestReview;
  @Inject @Named("modelEvaluationMaximumTrades")
  private final int evaluationMaximumTrades;

  public Evaluation create(
    TradeType tradeType, NeuralNetwork neuralNetwork, StockDataset evaluationDataset
  ) {
    return Evaluation.create(tradeType, neuralNetwork, evaluationDataset,
      buyReviewPeriod, sellReviewPeriod, dayLongestReview, evaluationMaximumTrades);
  }
}
