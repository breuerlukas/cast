package de.lukasbreuer.cast.core.neuralnetwork;

import com.clearspring.analytics.util.Lists;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public final class HistoryIterator implements MultiDataSetIterator {
  public static HistoryIterator create(
    List<Map.Entry<List<double[]>, Double>> dataset, Random random,
    int batchSize, int totalBatches
  ) {
    return new HistoryIterator(dataset, random, batchSize, totalBatches);
  }

  private final List<Map.Entry<List<double[]>, Double>> dataset;
  private final List<Map.Entry<INDArray, INDArray>> preparedDataset = Lists.newArrayList();
  private final Random random;
  private final int batchSize;
  private final int totalBatches;
  private final List<INDArray> labelsMaskList = Lists.newArrayList();
  private int currentBatch = 0;
  private int currentDatasetStep = 0;

  private HistoryIterator(
    List<Map.Entry<List<double[]>, Double>> dataset, Random random,
    int batchSize, int totalBatches
  ) {
    this.dataset = dataset;
    this.random = random;
    this.batchSize = batchSize;
    this.totalBatches = totalBatches;

    for (var entry : dataset) {
      preparedDataset.add(new AbstractMap.SimpleEntry<>(buildInputVector(entry.getKey()),
        buildOutputVector(entry.getValue())));
    }

    for (var i = 0; i < batchSize; i++) {
      labelsMaskList.add(buildLabelsMask());
    }
  }

  //TODO: REWRITE
  @Override
  public MultiDataSet next(int sampleSize) {
    INDArray inputSeq, outputSeq, labelsMaskSeq;
    int currentCount = 0;
    List<INDArray> inputSeqList = new ArrayList<>();
    List<INDArray> outputSeqList = new ArrayList<>();
    while (currentCount < sampleSize) {
      var entry = preparedDataset.get(currentDatasetStep);
      inputSeqList.add(entry.getKey());
      outputSeqList.add(entry.getValue());
      currentDatasetStep++;
      if (currentDatasetStep == dataset.size()) {
        currentDatasetStep = 0;
        Collections.shuffle(dataset, random);
      }
      currentCount++;
    }

    currentBatch++;

    inputSeq = Nd4j.vstack(inputSeqList);
    outputSeq = Nd4j.vstack(outputSeqList);
    labelsMaskSeq = Nd4j.vstack(labelsMaskList);

    return new org.nd4j.linalg.dataset.MultiDataSet(inputSeq, outputSeq, null,
      labelsMaskSeq);
  }

  /*private INDArray buildInputVector(List<double[]> data) {
    INDArray result = Nd4j.zeros(1, data.size() * data.get(0).length);
    for (int i = 0; i < data.size(); i++) {
      var dayVector = data.get(i);
      for (var j = 0; j < dayVector.length; j++) {
        result.putScalar(0, (long) i * dayVector.length + j, dayVector[j]);
      }
    }
    return result;
  }

  public INDArray buildOutputVector(double data) {
    INDArray result = Nd4j.zeros(1, 2);
    result.putScalar(0, 0, data);
    result.putScalar(0, 1, 1 - data);
    return result;
  }*/

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

  private static final int REVIEW_PERIOD = 7 * 4;

  public INDArray buildOutputVector(double data) {
    INDArray result = Nd4j.zeros(1, 2, REVIEW_PERIOD);
    result.putScalar(0, 0, REVIEW_PERIOD - 1, data);
    result.putScalar(0, 1, REVIEW_PERIOD - 1, 1 - data);
    return result;
  }

  private INDArray buildLabelsMask() {
    INDArray result = Nd4j.zeros(1, REVIEW_PERIOD);
    result.putScalar(0, REVIEW_PERIOD - 1, 1);
    return result;
  }

  /*private INDArray buildInputVector(List<double[]> data) {
    INDArray result = Nd4j.zeros(1, data.size() * data.get(0).length);
    for (int i = 0; i < data.size(); i++) {
      var dayVector = data.get(i);
      for (var j = 0; j < dayVector.length; j++) {
        result.putScalar(0, (long) i * dayVector.length + j, dayVector[j]);
      }
    }
    return result;
  }

  public INDArray buildOutputVector(double data) {
    INDArray result = Nd4j.zeros(1, 2);
    result.putScalar(0, 0, data);
    result.putScalar(0, 1, 1 - data);
    return result;
  }*/

  @Override
  public void reset() {
    currentBatch = 0;
  }

  @Override
  public boolean resetSupported() {
    return true;
  }

  @Override
  public boolean asyncSupported() {
    return false;
  }

  @Override
  public boolean hasNext() {
    return currentBatch < totalBatches;
  }

  @Override
  public MultiDataSet next() {
    return next(batchSize);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported");
  }

  @Override
  public void setPreProcessor(MultiDataSetPreProcessor multiDataSetPreProcessor) {

  }

  @Override
  public MultiDataSetPreProcessor getPreProcessor() {
    return null;
  }
}
