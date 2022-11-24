package de.lukasbreuer.stockalgorithm.core.neuralnetwork.dataset;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.stockalgorithm.core.neuralnetwork.ModelState;
import de.lukasbreuer.stockalgorithm.core.symbol.Symbol;
import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public final class StockDataset {
  private final Symbol symbol;
  private final TradeType tradeType;
  private final ModelState modelState;
  private final List<Map.Entry<List<double[]>, Double>> dataset = Lists.newArrayList();

  public void build() {

  }

  public List<Map.Entry<List<double[]>, Double>> raw() {
    return List.copyOf(dataset);
  }
}
