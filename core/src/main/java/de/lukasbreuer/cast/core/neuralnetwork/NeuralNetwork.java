package de.lukasbreuer.cast.core.neuralnetwork;

import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.*;
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
    int lstmLayerSize = 128;
    var configuration = new NeuralNetConfiguration.Builder()
      .seed(seed)
      .weightInit(WeightInit.XAVIER)
      .updater(new RmsProp(0.001))
      .list()
      .layer(new LSTM.Builder().nIn(26).nOut(lstmLayerSize)
        .activation(Activation.TANH).build())
      .layer(new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
        .activation(Activation.TANH).build())
      .layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
        .activation(Activation.SOFTMAX)
        .nIn(lstmLayerSize).nOut(outputSize).build())
      .build();
    network = new MultiLayerNetwork(configuration);
    network.init();
  }

  /*public void build() {
    var configurationBuilder = new NeuralNetConfiguration.Builder();
    configureNetworkProperties(configurationBuilder);
    var layerConfigurationBuilder = configurationBuilder.list();
    configureNetworkLayers(layerConfigurationBuilder);
    var configuration = layerConfigurationBuilder.build();
    network = new MultiLayerNetwork(configuration);
    network.init();
  }*/

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

  public void train(Runnable callback) {
    var epochCount = 0;
    while (epochCount < epochs) {
      network.fit(dataSetIterator);
      dataSetIterator.reset();
      epochCount++;
      System.out.println("Finished Epoch " + epochCount);
      if (epochCount % 10 == 0) {
        callback.run();
      }
    }
  }

  public float evaluate(
    int index, Map.Entry<List<double[]>, Double> entry, boolean print
  ) {
    var input = buildInputVector(entry.getKey());
    //var prediction = network.output(input, false).getFloat(0) * 100;
    var prediction = network.output(input, false).getFloat(0, 0, 7 * 4 - 1) * 100;
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
    INDArray result = Nd4j.zeros(1, data.get(0).length, data.size());
    for (int i = 0; i < data.size(); i++) {
      var dayVector = data.get(i);
      for (var j = 0; j < dayVector.length; j++) {
        result.putScalar(0, j, i, dayVector[j]);
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

  /*public void build() {
    try {
      MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
        .seed(seed)
        .updater(new Adam())
        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
        .weightInit(WeightInit.XAVIER)
        .list()
        .layer(new ConvolutionLayer.Builder().kernelSize(3,3).stride(1,1).padding(1,1).activation(Activation.LEAKYRELU)
          .nIn(1).nOut(16).build())
        .layer(new BatchNormalization())
        .layer(new ConvolutionLayer.Builder().kernelSize(1,1).stride(1,1).padding(1,1).activation(Activation.LEAKYRELU)
          .nOut(32).build())
        .layer(new BatchNormalization())
        .layer(new ConvolutionLayer.Builder().kernelSize(3,3).stride(1,1).padding(1,1).activation(Activation.LEAKYRELU)
          .nOut(64).build())
        .layer(new BatchNormalization())
        .layer(new ConvolutionLayer.Builder().kernelSize(1,1).stride(1,1).padding(1,1).activation(Activation.LEAKYRELU)
          .nOut(outputSize).build())
        .layer(new BatchNormalization())
        .layer(new SubsamplingLayer.Builder().kernelSize(2,2).stride(2,2).poolingType(SubsamplingLayer.PoolingType.AVG).build())
        .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
          .name("output")
          .nOut(outputSize)
          .dropOut(0.8)
          .activation(Activation.SOFTMAX)
          .build())
        .setInputType(InputType.convolutionalFlat(30, 7 * 4, 1))
        .build();
      network = new MultiLayerNetwork(configuration);
      network.init();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }*/
}
