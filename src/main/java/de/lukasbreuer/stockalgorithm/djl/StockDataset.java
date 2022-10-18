package de.lukasbreuer.stockalgorithm.djl;

import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.dataset.Record;
import ai.djl.util.Progress;
import com.clearspring.analytics.util.Lists;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public final class StockDataset extends RandomAccessDataset {
  public static StockDataset create(List<Map.Entry<List<double[]>, Double>> data) {
    var processedData = Lists.<Map.Entry<float[][], Float>>newArrayList();
    for (var entry : data) {
      var inputDouble = entry.getKey().toArray(double[][]::new);
      var inputFloat = new float[inputDouble.length][inputDouble[0].length];
      for (var i = 0; i < inputDouble.length; i++) {
        for (var j = 0; j < inputDouble[i].length; j++) {
          inputFloat[i][j] = (float) inputDouble[i][j];
        }
      }
      processedData.add(new AbstractMap.SimpleEntry<>(inputFloat, entry.getValue().floatValue()));
    }
    return new StockDataset(new Builder().setSampling(32, true), processedData);
  }

  private final List<Map.Entry<float[][], Float>> data;

  private StockDataset(
    BaseBuilder<?> builder, List<Map.Entry<float[][], Float>> data
  ) {
    super(builder);
    this.data = data;
  }

  @Override
  public Record get(NDManager manager, long index) {
    var datum = new NDList();
    var label = new NDList();
    var entry = data.get((int) index);
    datum.add(manager.create(entry.getKey()));
    label.add(manager.create(entry.getValue()));
    datum.attach(manager);
    label.attach(manager);
    return new Record(datum, label);
  }

  @Override
  protected long availableSize() {
    return data.size();
  }

  @Override
  public void prepare(Progress progress) {
  }

  private static final class Builder extends BaseBuilder<Builder> {
    @Override
    protected Builder self() {
      return this;
    }
  }
}