package de.lukasbreuer.stockalgorithm.core.dataset.indicator;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.symbol.HistoryEntry;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public final class IndicatorRepository {
  private final List<HistoryEntry> data;
  private final List<Indicator> indicators = Lists.newArrayList();

  public void fill() {
    indicators.add(ChangeIndicator.create(data));
    indicators.add(MovingAverageIndicator.create(data));
    indicators.add(ChangeRateIndicator.create(data));
    indicators.add(RelativeStrengthIndicator.create(data));
    indicators.add(CommodityChannelIndicator.create(data));
    indicators.add(StochasticOscillatorIndicator.create(data));
    indicators.add(AverageRangeIndicator.create(data));
    indicators.add(BullishPatternIndicator.create(data));
    indicators.add(BearishPatternIndicator.create(data));
  }

  public <T extends Indicator> T find(Class<T> type) {
    return (T) indicators.stream()
      .filter(indicator -> indicator.getClass() == type)
      .collect(Collectors.toList());
  }
}
