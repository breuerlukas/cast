package de.lukasbreuer.cast.core.neuralnetwork;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.core.trade.TradeType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.IUpdater;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NeuralNetworkFactory {
  @Inject @Named("networkEpochs")
  private final int epochs;
  @Inject
  private final WeightInit weightInit;
  @Inject
  private final Activation activation;
  @Inject
  private final IUpdater updater;
  @Inject @Named("networkLearningRate")
  private final float learningRate;
  @Inject @Named("networkDropoutRate")
  private final float dropoutRate;
  @Inject @Named("modelInputSizePerDay")
  private final int inputSizePerDay;
  @Inject @Named("modelBuyReviewPeriod")
  private final int buyReviewPeriod;
  @Inject @Named("modelSellReviewPeriod")
  private final int sellReviewPeriod;
  @Inject @Named("networkHiddenNeurons")
  private final int[] hiddenNeurons;

  public NeuralNetwork create(
    TradeType tradeType, HistoryIterator historyIterator, int seed
  ) {
    return NeuralNetwork.create(seed, epochs, weightInit, activation, updater,
      learningRate, dropoutRate, inputSizePerDay * (tradeType.isBuy() ?
        buyReviewPeriod : sellReviewPeriod), hiddenNeurons, 2, historyIterator);
  }
}
