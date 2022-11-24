package de.lukasbreuer.stockalgorithm.core.dl4j;

import lombok.RequiredArgsConstructor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.MultiDataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

@RequiredArgsConstructor(staticName = "create")
public final class HistoryIterator implements MultiDataSetIterator {
  private final List<Map.Entry<List<double[]>, Double>> dataset;
  private final Random random;
  private final int batchSize;
  private final int totalBatches;
  private int currentBatch = 0;
  private int currentDatasetStep = 0;

  @Override
  public MultiDataSet next(int sampleSize) {
    INDArray inputSeq, outputSeq;
    int currentCount = 0;
    List<INDArray> inputSeqList = new ArrayList<>();
    List<INDArray> outputSeqList = new ArrayList<>();
    while (currentCount < sampleSize) {
      var entry = dataset.get(currentDatasetStep);
      inputSeqList.add(buildInputVector(entry.getKey()));
      outputSeqList.add(buildOutputVector(entry.getValue()));
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

    INDArray[] inputs = new INDArray[] {inputSeq};
    INDArray[] labels = new INDArray[] {outputSeq};
    return new org.nd4j.linalg.dataset.MultiDataSet(inputs, labels, null, null);
  }

  public INDArray buildInputVector(List<double[]> data) {
    INDArray result = Nd4j.zeros(1, data.size() * data.get(0).length);
    for (int i = 0; i < data.size(); i++) {
      var dayVector = data.get(i);
      for (var j = 0; j < dayVector.length; j++) {
        result.putScalar(0, i * dayVector.length +  j, dayVector[j]);
      }
    }
    return result;
  }

  public INDArray buildOutputVector(double data) {
    INDArray result = Nd4j.zeros(1, 2);
    result.putScalar(0, 0, data);
    result.putScalar(0, 1, 1 - data);
    return result;
  }

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
