package de.lukasbreuer.stockalgorithm.train.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.train.dataset.indicator.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(staticName = "create")
public final class DatasetDay {
  private final int index;
  private final IndicatorRepository indicatorRepository;
  private double[] data;

  public void build() {
    var calculations = Lists.<Double>newArrayList();
    calculations.addAll(createReviewIndicatorBundle(ChangeIndicator.class, new int[] {0, 1, 2, 3}));
    calculations.addAll(createRegularReviewIndicatorBundle(MovingAverageIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(ChangeRateIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(RelativeStrengthIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(CommodityChannelIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(StochasticOscillatorIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(AverageRangeIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(BalanceVolumeIndicator.class));
    calculations.addAll(createRegularReviewIndicatorBundle(DirectionalMovementIndicator.class));
    calculations.add(indicatorRepository.find(BullishPatternIndicator.class).calculate(index));
    calculations.add(indicatorRepository.find(BearishPatternIndicator.class).calculate(index));
    data = calculations.stream().mapToDouble(value -> value).toArray();
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

  public double[] raw() {
    return data;
  }
}
