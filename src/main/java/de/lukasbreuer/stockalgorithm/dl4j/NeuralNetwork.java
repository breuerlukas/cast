package de.lukasbreuer.stockalgorithm.dl4j;

import lombok.RequiredArgsConstructor;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ops.random.impl.UniformDistribution;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public final class NeuralNetwork {
  private final int seed;
  private final float learningRate;
  private final float dropoutRate;
  private final int iterations;
  private final int epochs;
  private final int inputSize;
  private final int[] hiddenLayers;
  private final int outputSize;
  private final HistoryIterator dataSetIterator;
  private MultiLayerNetwork network;

  public void build() {
    NeuralNetConfiguration.ListBuilder configurationBuilder = new NeuralNetConfiguration.Builder()
      .seed(seed)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .weightInit(WeightInit.RELU)
      .learningRate(1e-6)
      .updater(Updater.NESTEROVS)
      .iterations(5)
      .list();
    configurationBuilder.layer(0, new DenseLayer.Builder()
      .nIn(inputSize)
      .nOut(hiddenLayers[0])
      .activation(Activation.RELU)
      .build());
    for (var i = 0; i < hiddenLayers.length - 1; i++) {
      configurationBuilder.layer(i + 1, new DenseLayer.Builder()
        .nIn(hiddenLayers[i])
        .nOut(hiddenLayers[i + 1])
        .activation(Activation.RELU)
        .build());
    }
    configurationBuilder.layer(hiddenLayers.length, new OutputLayer.Builder()
      .nIn(hiddenLayers[hiddenLayers.length - 1])
      .nOut(outputSize)
      .activation(Activation.SOFTMAX)
      .build());
    var configuration = configurationBuilder
      .backprop(true).pretrain(false)
      .build();
    network = new MultiLayerNetwork(configuration);
    network.init();
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
    var prediction = network.output(input, false).getFloat(0);
    var prefix = "";
    if (prediction > 0.1f) {
      prefix = "\u001B[31m";
    }
    System.out.println(prefix + index + ": " + entry.getValue() + " <-> " + prediction + "\u001B[0m");
    return prediction;
  }
}
