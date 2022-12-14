package de.lukasbreuer.stockalgorithm.core.neuralnetwork;

import lombok.RequiredArgsConstructor;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public final class NeuralNetwork {
  private final int seed;
  private final int epochs;
  private final WeightInit weightInit;
  private final Activation activation;
  private final IUpdater updater;
  private final float learningRate;
  private final float dropoutRate;
  private final int inputSize;
  private final int[] hiddenLayers;
  private final int outputSize;
  private final HistoryIterator dataSetIterator;
  private MultiLayerNetwork network;

  public void build() {
    NeuralNetConfiguration.Builder configurationBuilder = new NeuralNetConfiguration.Builder();
    configurationBuilder.seed(seed);
    configurationBuilder.weightInit(weightInit);
    configurationBuilder.activation(activation);
    configurationBuilder.updater(updater);
    if (learningRate > 0) {
      configurationBuilder.l2(learningRate);
    }
    if (dropoutRate > 0) {
      configurationBuilder.dropOut(dropoutRate);
    }
    NeuralNetConfiguration.ListBuilder listConfigurationBuilder = configurationBuilder.list();
    listConfigurationBuilder.layer(new DenseLayer.Builder()
      .nIn(inputSize)
      .nOut(hiddenLayers[0])
      .build());
    for (var i = 0; i < hiddenLayers.length - 1; i++) {
      listConfigurationBuilder.layer(new DenseLayer.Builder()
        .nIn(hiddenLayers[i])
        .nOut(hiddenLayers[i + 1])
        .build());
    }
    listConfigurationBuilder.layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .nIn(hiddenLayers[hiddenLayers.length - 1])
      .nOut(outputSize)
      .activation(Activation.SOFTMAX)
      .build());
    var configuration = listConfigurationBuilder.build();
    network = new MultiLayerNetwork(configuration);
    network.init();
  }

  public void load(String path) throws Exception {
    network = MultiLayerNetwork.load(new File(path), true);
  }

  public void train(Map.Entry<List<double[]>, Double> entry) {
    var epochCount = 0;
    while (epochCount < epochs) {
      network.fit(dataSetIterator);
      dataSetIterator.reset();
      evaluate(0, entry);
      epochCount++;
      System.out.println("Finished Epoch " + epochCount);
    }
  }

  public float evaluate(int index, Map.Entry<List<double[]>, Double> entry) {
    var input = dataSetIterator.buildInputVector(entry.getKey());
    var prediction = network.output(input, false).getFloat(0) * 100;
    var prefix = "";
    if (prediction > 30f) {
      prefix = "\u001B[31m";
    }
    System.out.println(prefix + index + ": " + entry.getValue() + " <-> " + prediction + "\u001B[0m");
    return prediction;
  }

  public void save(String path) throws Exception {
    network.save(new File(path));
  }
}
