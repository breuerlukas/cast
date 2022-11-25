package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset.indicator.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(staticName = "create")
public final class DatasetDay {
  private final int index;
  private final IndicatorRepository indicatorRepository;

  public double[] build() {
    var data = Lists.<Double>newArrayList();
    data.addAll(createReviewIndicatorBundle(ChangeIndicator.class, new int[] {1, 2, 3}));
    data.addAll(createRegularReviewIndicatorBundle(MovingAverageIndicator.class));
    data.addAll(createRegularReviewIndicatorBundle(ChangeRateIndicator.class));
    data.addAll(createRegularReviewIndicatorBundle(RelativeStrengthIndicator.class));
    data.addAll(createRegularReviewIndicatorBundle(CommodityChannelIndicator.class));
    data.addAll(createRegularReviewIndicatorBundle(StochasticOscillatorIndicator.class));
    data.addAll(createRegularReviewIndicatorBundle(AverageRangeIndicator.class));
    data.add(indicatorRepository.find(BullishPatternIndicator.class).calculate(index));
    data.add(indicatorRepository.find(BearishPatternIndicator.class).calculate(index));
    return data.stream().mapToDouble(value -> value).toArray();
  }

  private <T extends ReviewIndicator> List<Double> createRegularReviewIndicatorBundle(
    Class<T> indicatorType
  ) {
    return createReviewIndicatorBundle(indicatorType, new int[] {6, 9, 14, 21});
  }

  private <T extends ReviewIndicator> List<Double> createReviewIndicatorBundle(
    Class<T> indicatorType, int[] reviewCompilation
  ) {
    var bundle = Lists.<Double>newArrayList();
    for (var review : reviewCompilation) {
      bundle.add(indicatorRepository.find(indicatorType).calculate(index, review));
    }
    return bundle;
  }
}
