package de.lukasbreuer.cast.train.neuralnetwork;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;

@RequiredArgsConstructor(staticName = "create")
public final class NeuralNetworkModule extends AbstractModule {
  private static final int NETWORK_EPOCHS = 20;

  @Provides
  @Singleton
  @Named("networkEpochs")
  int provideNetworkEpochs() {
    return NETWORK_EPOCHS;
  }

  private static final WeightInit NETWORK_WEIGHT_INIT = WeightInit.XAVIER;

  @Provides
  @Singleton
  WeightInit provideNetworkWeightInit() {
    return NETWORK_WEIGHT_INIT;
  }

  private static final Activation NETWORK_ACTIVATION = Activation.TANH;

  @Provides
  @Singleton
  Activation provideNetworkActivation() {
    return NETWORK_ACTIVATION;
  }

  private static final IUpdater NETWORK_UPDATER = new Sgd(0.1);

  @Provides
  @Singleton
  IUpdater provideNetworkUpdater() {
    return NETWORK_UPDATER;
  }

  private static final float NETWORK_LEARNING_RATE = -1;

  @Provides
  @Singleton
  @Named("networkLearningRate")
  float provideNetworkLearningRate() {
    return NETWORK_LEARNING_RATE;
  }

  private static final float NETWORK_DROPOUT_RATE = -1;

  @Provides
  @Singleton
  @Named("networkDropoutRate")
  float provideNetworkDropoutRate() {
    return NETWORK_DROPOUT_RATE;
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
