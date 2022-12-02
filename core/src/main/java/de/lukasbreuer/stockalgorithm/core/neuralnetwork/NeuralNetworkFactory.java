package de.lukasbreuer.stockalgorithm.core.neuralnetwork;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.stockalgorithm.core.dataset.HistoryIterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NeuralNetworkFactory {
  @Inject @Named("networkEpochs")
  private final int epochs;
  @Inject @Named("networkLearningRate")
  private final int learningRate;
  @Inject @Named("networkDropoutRate")
  private final int dropoutRate;
  @Inject @Named("networkIterations")
  private final int iterations;

  public NeuralNetwork create(HistoryIterator historyIterator, int seed) {
    return NeuralNetwork.create(seed, epochs, learningRate, dropoutRate,
      iterations, -1, new int[0], -1, historyIterator);
  }
}
