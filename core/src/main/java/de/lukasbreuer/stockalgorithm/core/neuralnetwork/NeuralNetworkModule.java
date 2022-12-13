package de.lukasbreuer.stockalgorithm.core.neuralnetwork;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public final class NeuralNetworkModule extends AbstractModule {
  private static final int NETWORK_EPOCHS = 20;

  @Provides
  @Singleton
  @Named("networkEpochs")
  int provideNetworkEpochs() {
    return NETWORK_EPOCHS;
  }

  private static final float NETWORK_LEARNING_RATE = 1e-4f;

  @Provides
  @Singleton
  @Named("networkLearningRate")
  float provideNetworkLearningRate() {
    return NETWORK_LEARNING_RATE;
  }

  private static final float NETWORK_DROPOUT_RATE = 1f;

  @Provides
  @Singleton
  @Named("networkDropoutRate")
  float provideNetworkDropoutRate() {
    return NETWORK_DROPOUT_RATE;
  }

  private static final int NETWORK_ITERATIONS = 1;

  @Provides
  @Singleton
  @Named("networkIterations")
  int provideNetworkIterations() {
    return NETWORK_ITERATIONS;
  }

  @Provides
  @Singleton
  @Named("networkInputNeurons")
  int provideNetworkInputNeurons(
    @Named("modelReviewPeriod") int reviewPeriod,
    @Named("modelInputSizePerDay") int inputSizePerDay) {
    return reviewPeriod * inputSizePerDay;
  }

  private static final int[] NETWORK_HIDDEN_NEURONS = new int[] {512, 512};

  @Provides
  @Singleton
  @Named("networkHiddenNeurons")
  int[] provideNetworkHiddenNeurons() {
    return NETWORK_HIDDEN_NEURONS;
  }

  private static final int NETWORK_OUTPUT_NEURONS = 2;

  @Provides
  @Singleton
  @Named("networkOutputNeurons")
  int provideNetworkOutputNeurons() {
    return NETWORK_OUTPUT_NEURONS;
  }
}
