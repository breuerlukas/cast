package de.lukasbreuer.cast.deploy.command;

import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.Model;
import de.lukasbreuer.cast.deploy.model.ModelCollection;

import java.util.UUID;

public final class ModelCommand extends Command {
  public static ModelCommand create(Log log, ModelCollection modelCollection) {
    return new ModelCommand(log, modelCollection);
  }

  private final ModelCollection modelCollection;

  private ModelCommand(Log log, ModelCollection modelCollection) {
    super(log, "model", new String[] {"models"}, new String[] {"stock", "add <stock> <buy model> " +
      "<sell model> <review period> <buy prediction minimum> <sell prediction minimum>",
      "update <stock> <parameter> <value>", "remove <stock>"});
    this.modelCollection = modelCollection;
  }

  @Override
  public boolean execute(String[] arguments) {
    if (arguments.length == 0) {
      return false;
    }
    if (arguments[0].equalsIgnoreCase("add")) {
      return executeAdd(arguments);
    }
    if (arguments[0].equalsIgnoreCase("update")) {
      return executeUpdate(arguments);
    }
    if (arguments[0].equalsIgnoreCase("remove")) {
      return executeRemove(arguments);
    }
    var stock = arguments[0].toUpperCase();
    modelCollection.findByStock(stock, model -> printModel(stock, model));
    return true;
  }

  private boolean executeAdd(String[] arguments) {
    if (arguments.length != 7) {
      return false;
    }
    var stock = arguments[1].toUpperCase();
    var buyModelPath = arguments[2];
    var sellModelPath = arguments[3];
    var reviewPeriod = Integer.parseInt(arguments[4]);
    var buyTradePredictionMinimum = Double.parseDouble(arguments[5]);
    var sellTradePredictionMaximum = Double.parseDouble(arguments[6]);
    modelCollection.addModel(Model.create(log(), UUID.randomUUID(), stock,
      buyModelPath, sellModelPath, reviewPeriod, buyTradePredictionMinimum,
      sellTradePredictionMaximum), success ->
      log().info("Stock " + stock + " has been successfully added"));
    return true;
  }

  private boolean executeUpdate(String[] arguments) {
    if (arguments.length != 4) {
      return false;
    }
    var stock = arguments[1].toUpperCase();
    var parameter = arguments[2];
    var value = arguments[3];
    modelCollection.findByStock(stock, model -> updateModel(stock, model, parameter, value));
    return true;
  }

  private boolean executeRemove(String[] arguments) {
    if (arguments.length != 2) {
      return false;
    }
    var stock = arguments[1].toUpperCase();
    modelCollection.removeModel(stock, success ->
      log().info("Model " + stock + " has been successfully removed"));
    return true;
  }

  private void updateModel(String stock, Model model, String parameter, String value) {
    model.updateParameter(parameter, value);
    modelCollection.updateModel(model, success ->
      log().info("Successfully updated " + stock + "'s model parameter " + parameter +
      " to " + value));
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
