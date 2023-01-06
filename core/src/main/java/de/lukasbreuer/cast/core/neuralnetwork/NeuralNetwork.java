package de.lukasbreuer.cast.core.neuralnetwork;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class NeuralNetwork {
  public static NeuralNetwork createAndLoad(String path) throws Exception {
    var neuralNetwork = new NeuralNetwork();
    neuralNetwork.load(path);
    return neuralNetwork;
  }

  public static NeuralNetwork create(
    int seed, int epochs, WeightInit weightInit, Activation activation,
    IUpdater updater, float learningRate, float dropoutRate, int inputSize,
    int[] hiddenLayers, int outputSize, HistoryIterator dataSetIterator
  ) {
    return new NeuralNetwork(seed, epochs, weightInit, activation, updater,
      learningRate, dropoutRate, inputSize, hiddenLayers, outputSize,
      dataSetIterator);
  }

  private int seed;
  private int epochs;
  private WeightInit weightInit;
  private Activation activation;
  private IUpdater updater;
  private float learningRate;
  private float dropoutRate;
  private int inputSize;
  private int[] hiddenLayers;
  private int outputSize;
  private HistoryIterator dataSetIterator;
  private MultiLayerNetwork network;

  private NeuralNetwork(
    int seed, int epochs, WeightInit weightInit, Activation activation,
    IUpdater updater, float learningRate, float dropoutRate, int inputSize,
    int[] hiddenLayers, int outputSize, HistoryIterator dataSetIterator
  ) {
    this.seed = seed;
    this.epochs = epochs;
    this.weightInit = weightInit;
    this.activation = activation;
    this.updater = updater;
    this.learningRate = learningRate;
    this.dropoutRate = dropoutRate;
    this.inputSize = inputSize;
    this.hiddenLayers = hiddenLayers;
    this.outputSize = outputSize;
    this.dataSetIterator = dataSetIterator;
  }

  private NeuralNetwork() {

  }

  public void build() {
    var configurationBuilder = new NeuralNetConfiguration.Builder();
    configureNetworkProperties(configurationBuilder);
    var layerConfigurationBuilder = configurationBuilder.list();
    configureNetworkLayers(layerConfigurationBuilder);
    var configuration = layerConfigurationBuilder.build();
    network = new MultiLayerNetwork(configuration);
    network.init();
  }

  private void configureNetworkProperties(
    NeuralNetConfiguration.Builder configurationBuilder
  ) {
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
  }

  private void configureNetworkLayers(
    NeuralNetConfiguration.ListBuilder layerConfigurationBuilder
  ) {
    layerConfigurationBuilder.layer(new DenseLayer.Builder()
      .nIn(inputSize)
      .nOut(hiddenLayers[0])
      .build());
    for (var i = 0; i < hiddenLayers.length - 1; i++) {
      layerConfigurationBuilder.layer(new DenseLayer.Builder()
        .nIn(hiddenLayers[i])
        .nOut(hiddenLayers[i + 1])
        .build());
    }
    layerConfigurationBuilder.layer(new OutputLayer.Builder(
      LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
      .nIn(hiddenLayers[hiddenLayers.length - 1])
      .nOut(outputSize)
      .activation(Activation.SOFTMAX)
      .build());
  }

  public void load(String path) throws Exception {
    network = MultiLayerNetwork.load(new File(path), true);
  }

  public void train(Map.Entry<List<double[]>, Double> entry) {
    var epochCount = 0;
    while (epochCount < epochs) {
      network.fit(dataSetIterator);
      dataSetIterator.reset();
      evaluate(0, entry, true);
      epochCount++;
      System.out.println("Finished Epoch " + epochCount);
    }
  }

  public float evaluate(
    int index, Map.Entry<List<double[]>, Double> entry, boolean print
  ) {
    var input = buildInputVector(entry.getKey());
    var prediction = network.output(input, false).getFloat(0) * 100;
    var prefix = "";
    if (prediction > 30f) {
      prefix = "\u001B[31m";
    }
    if (print) {
      System.out.println(prefix + index + ": " + entry.getValue() + " <-> " +
        prediction + "\u001B[0m");
    }
    return prediction;
  }

  private INDArray buildInputVector(List<double[]> data) {
    INDArray result = Nd4j.zeros(1, data.size() * data.get(0).length);
    for (int i = 0; i < data.size(); i++) {
      var dayVector = data.get(i);
      for (var j = 0; j < dayVector.length; j++) {
        result.putScalar(0, (long) i * dayVector.length + j, dayVector[j]);
      }
    }
    return result;
  }

  public void save(String path) throws Exception {
    var file = new File(path);
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    network.save(file);
  }
}
