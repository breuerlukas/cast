package de.lukasbreuer.stockalgorithm.dl4j;

import lombok.RequiredArgsConstructor;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.random.impl.UniformDistribution;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Arrays;
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
  private final int lstmLayerSize;
  private final int outputSize;
  private final HistoryIterator dataSetIterator;
  private MultiLayerNetwork network;

  public void build() {
    /*var configuration = new NeuralNetConfiguration.Builder()
      .weightInit(WeightInit.XAVIER)
      .learningRate(learningRate)
      //.dropOut(dropoutRate)
      .updater(Updater.ADAM)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(iterations)
      .seed(seed)
      .graphBuilder()
      .addInputs("trainFeatures")
      .setOutputs("predictMortality")
      .addLayer("L1", new LSTM.Builder()
        .nIn(inputSize)
        .nOut(lstmLayerSize)
        .activation(Activation.SOFTSIGN)
        .build(), "trainFeatures")
      .addLayer("predictMortality", new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
        //.activation(Activation.SOFTMAX)
        .nIn(lstmLayerSize).nOut(outputSize).build(), "L1")
      .pretrain(false).backprop(true)
      .build();
    network = new ComputationGraph(configuration);
    network.init();
    network.setListeners(new ScoreIterationListener(500));*/
    MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
      .seed(seed)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .learningRate(0.01)
      .list()
      .layer(0, new DenseLayer.Builder()
        .nIn(inputSize)
        .nOut(lstmLayerSize)
        .weightInit(WeightInit.XAVIER)
        .activation(Activation.TANH)
        .build())
      .layer(1, new DenseLayer.Builder()
        .nIn(lstmLayerSize)
        .nOut(lstmLayerSize)
        .weightInit(WeightInit.XAVIER)
        .activation(Activation.TANH)
        .build())
      .layer(2, new DenseLayer.Builder()
        .nIn(lstmLayerSize)
        .nOut(lstmLayerSize)
        .weightInit(WeightInit.XAVIER)
        .activation(Activation.TANH)
        .build())
      .layer(3, new DenseLayer.Builder()
        .nIn(lstmLayerSize)
        .nOut(lstmLayerSize)
        .weightInit(WeightInit.XAVIER)
        .activation(Activation.TANH)
        .build())
      .layer(4, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
        .nIn(lstmLayerSize)
        .nOut(outputSize)
        .weightInit(WeightInit.XAVIER)
        .build())
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
      evaluate(entry, 0);
      epochCount++;
      System.out.println("Finished Epoch " + epochCount);
    }
  }

  public float evaluate(Map.Entry<List<double[]>, Double> entry, int index) {
    var input = /*dataSetIterator.buildOutputVector(entry.getValue());*/dataSetIterator.buildInputVector(entry.getKey());
    var prediction = network.output(input).getFloat(0);
    var prefix = "";
    if (prediction > 0.1f) {
      prefix = "\u001B[31m";
    }
    System.out.println(prefix + index + ":" + entry.getValue() + " <-> " + prediction + "\u001B[0m");
    return prediction;
  }
}
