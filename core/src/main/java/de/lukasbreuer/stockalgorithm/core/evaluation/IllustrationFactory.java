package de.lukasbreuer.stockalgorithm.core.evaluation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.stockalgorithm.core.dataset.StockDataset;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class IllustrationFactory {
  @Inject @Named("modelEvaluationPeriod")
  private final int evaluationPeriod;
  @Inject @Named("modelTradeGeneralisationStepSize")
  private final int generalisationStepSize;

  public Illustration create(
    TradeType tradeType, Evaluation evaluation, StockDataset dataset, int seed
  ) {
    return Illustration.create(tradeType, evaluation, dataset, seed,
      evaluationPeriod, generalisationStepSize);
  }
}
