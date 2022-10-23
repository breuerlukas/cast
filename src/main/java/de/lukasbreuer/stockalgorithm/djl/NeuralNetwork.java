package de.lukasbreuer.stockalgorithm.djl;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.TrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.loss.Loss;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public final class NeuralNetwork {
  public static NeuralNetwork create(
    List<Map.Entry<List<double[]>, Double>> data, String modelName, int inputNeurons,
    int[] hiddenNeurons, int outputNeurons
  ) throws Exception {
    var model = Model.newInstance(modelName);
    model.setBlock(createNetwork(inputNeurons, hiddenNeurons, outputNeurons));
    //var modelPath = Paths.get("build/neuralnetwork");
    //model.load(modelPath);
    return create(StockDataset.create(data),
      inputNeurons, hiddenNeurons, outputNeurons, model);
  }

  private final StockDataset dataset;
  private final int inputNeurons;
  private final int[] hiddenNeurons;
  private final int outputNeurons;
  private final Model model;

  public void train(int epochs, Map.Entry<List<double[]>, Double> entry) throws Exception {
    var trainer = model.newTrainer(configureTrainer());
    trainer.initialize(new Shape(inputNeurons));
    for (var i = 0; i < epochs; i++) {
      EasyTrain.fit(trainer, i, dataset, null);
      System.out.println(i + 1);
      evaluate(0, entry);
    }
    safe();
  }

  private TrainingConfig configureTrainer() {
    return new DefaultTrainingConfig(
      Loss.sigmoidBinaryCrossEntropyLoss())
      //Loss.softmaxCrossEntropyLoss("SOFTMAX", 1, -1, true, true))
      .addEvaluator(new Accuracy());
  }

  public double evaluate(int index, Map.Entry<List<double[]>, Double> entry) throws Exception {
    var inputDouble = entry.getKey().toArray(double[][]::new);
    var inputFloat = new float[inputDouble.length][inputDouble[0].length];
    for (var i = 0; i < inputDouble.length; i++) {
      for (var j = 0; j < inputDouble[i].length; j++) {
        inputFloat[i][j] = (float) inputDouble[i][j];
      }
    }
    var predictor = model.newPredictor(new StockTranslator());
    var prediction = predictor.predict(NDManager.newBaseManager().create(inputFloat))[0];
    var prefix = "";
    if (prediction > 0.1f) {
      prefix = "\u001B[31m";
    }
    System.out.println(prefix + index + ": " + entry.getValue() + " <-> " + prediction + "\u001B[0m");
    return prediction;
  }

  public void safe() throws Exception {
    var modelPath = Paths.get("build/neuralnetwork");
    Files.createDirectories(modelPath);
    model.save(modelPath, model.getName());
  }

  private static SequentialBlock createNetwork(
    int inputNeurons, int[] hiddenNeurons, int outputNeurons
  ) {
    var block = new SequentialBlock();
    block.add(Blocks.batchFlattenBlock(inputNeurons));
    block.add(Activation::relu);
    for (var hiddenSize : hiddenNeurons) {
      block.add(Linear.builder().setUnits(hiddenSize).build());
      block.add(Activation::relu);
    }
    block.add(Linear.builder().setUnits(outputNeurons).build());
    return block;
  }

  public final class StockTranslator implements Translator<NDArray, double[]> {
    @Override
    public NDList processInput(TranslatorContext ctx, NDArray input) {
      return new NDList(input);
    }

    @Override
    public double[] processOutput(TranslatorContext ctx, NDList list) {
      //return Arrays.stream(list.get(0).toArray())
      return Arrays.stream(list.singletonOrThrow().toArray())
        .mapToDouble(Number::doubleValue)
        .map(value -> (float) (1 / (1 + Math.exp(-value))))
        .toArray();
    }

    @Override
    public Batchifier getBatchifier() {
      return Batchifier.STACK;
    }
  }
}