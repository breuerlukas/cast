package de.lukasbreuer.stockalgorithm.deploy.command;

import de.lukasbreuer.stockalgorithm.core.command.Command;
import de.lukasbreuer.stockalgorithm.core.log.Log;
import de.lukasbreuer.stockalgorithm.deploy.model.Model;
import de.lukasbreuer.stockalgorithm.deploy.model.ModelCollection;

public final class ModelCommand extends Command {
  public static ModelCommand create(Log log, ModelCollection modelCollection) {
    return new ModelCommand(log, modelCollection);
  }

  private final ModelCollection modelCollection;

  private ModelCommand(Log log, ModelCollection modelCollection) {
    super(log, "model", new String[] {"stock"});
    this.modelCollection = modelCollection;
  }

  @Override
  public boolean execute(String[] arguments) {
    if (arguments.length != 1) {
      return false;
    }
    var stock = arguments[0].toUpperCase();
    modelCollection.findByStock(stock, model -> printModel(stock, model));
    return true;
  }

  private void printModel(String stock, Model model) {
    log().info("Model (" + stock + "): ");
    log().info(" - Buy model: " + model.buyModelPath());
    log().info(" - Sell model: " + model.sellModelPath());
    log().info(" - Review period: " + model.reviewPeriod());
    log().info(" - Buy trade prediction minimum: " + model.buyTradePredictionMinimum());
    log().info(" - Sell trade prediction minimum: " + model.sellTradePredictionMinimum());
  }
}
