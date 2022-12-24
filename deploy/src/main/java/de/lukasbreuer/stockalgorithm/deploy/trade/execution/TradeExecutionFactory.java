package de.lukasbreuer.stockalgorithm.deploy.trade.execution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.model.ModelCollection;
import de.lukasbreuer.stockalgorithm.deploy.portfolio.Stock;
import de.lukasbreuer.stockalgorithm.deploy.trade.TradeCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class TradeExecutionFactory {
  @Inject @Named("deployLog")
  private final Log log;
  @Inject
  private final ModelCollection modelCollection;
  @Inject
  private final TradeCollection tradeCollection;
  @Inject @Named("investopediaUsername")
  private final String investopediaUsername;
  @Inject @Named("investopediaPassword")
  private final String investopediaPassword;
  @Inject @Named("investopediaGame")
  private final String investopediaGame;

  public TradeExecution create(Stock stock) {
    return TradeExecution.create(log, modelCollection, tradeCollection,
      investopediaUsername, investopediaPassword, investopediaGame, stock);
  }
}