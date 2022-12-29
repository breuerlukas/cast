package de.lukasbreuer.cast.deploy.trade.execution;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import de.lukasbreuer.cast.deploy.portfolio.Stock;
import de.lukasbreuer.cast.deploy.trade.TradeCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class TradeExecutionFactory {
  @Inject
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

  public void createAndInitialize(
          Stock stock, Consumer<TradeExecution> futureExecution
  ) {
    TradeExecution.createAndInitialize(log, modelCollection, tradeCollection,
      investopediaUsername, investopediaPassword, investopediaGame, stock, futureExecution);
  }

  public TradeExecution create(Stock stock) {
    return TradeExecution.create(log, modelCollection, tradeCollection,
      investopediaUsername, investopediaPassword, investopediaGame, stock);
  }
}