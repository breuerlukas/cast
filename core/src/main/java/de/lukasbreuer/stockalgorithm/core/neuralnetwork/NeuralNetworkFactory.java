package de.lukasbreuer.stockalgorithm.core.neuralnetwork;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NeuralNetworkFactory {
  @Inject @Named("networkEpochs")
  private final int epochs;
  @Inject @Named("networkLearningRate")
  private final float learningRate;
  @Inject @Named("networkDropoutRate")
  private final float dropoutRate;
  @Inject @Named("networkIterations")
  private final int iterations;
  @Inject @Named("networkInputNeurons")
  private final int inputNeurons;
  @Inject @Named("networkHiddenNeurons")
  private final int[] hiddenNeurons;
  @Inject @Named("networkOutputNeurons")
  private final int outputNeurons;

  public NeuralNetwork create(HistoryIterator historyIterator, int seed) {
    return NeuralNetwork.create(seed, epochs, learningRate, dropoutRate,
      iterations, inputNeurons, hiddenNeurons, outputNeurons, historyIterator);
  }
}
