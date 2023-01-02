package de.lukasbreuer.cast.access.model;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.model.Model;
import de.lukasbreuer.cast.deploy.model.ModelCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModelController {
  private final Log log;
  private final ModelCollection modelCollection;

  @RequestMapping(path = "/model/add", method = RequestMethod.POST)
  public void addModel(
    @RequestBody Map<String, Object> input
  ) {
    modelCollection.addModel(Model.create(log, UUID.randomUUID(),
      (String) input.get("stock"), (String) input.get("buyModelPath"),
      (String) input.get("sellModelPath"), Integer.parseInt((String) input.get("reviewPeriod")),
      Double.parseDouble((String) input.get("buyTradePredictionMinimum")),
      Double.parseDouble((String) input.get("sellTradePredictionMinimum"))), success -> {});
  }

  @RequestMapping(path = "/model/update", method = RequestMethod.POST)
  public void updateModel(
    @RequestBody Map<String, Object> input
  ) {
    modelCollection.findByStock((String) input.get("stock"),
      model -> updateModel(input, model));
  }

  private void updateModel(Map<String, Object> input, Model model) {
    updateParameter(input, model, "buyModelPath");
    updateParameter(input, model, "sellModelPath");
    updateParameter(input, model, "reviewPeriod");
    updateParameter(input, model, "buyTradePredictionMinimum");
    updateParameter(input, model, "sellTradePredictionMinimum");
    modelCollection.updateModel(model, success -> {});
  }

  private void updateParameter(Map<String, Object> input, Model model, String parameter) {
    model.updateParameter(parameter, (String) input.get(parameter));
  }

  @RequestMapping(path = "/model/exists", method = RequestMethod.POST)
  public CompletableFuture<Map<String, Object>> modelExists(
    @RequestBody Map<String, Object> input
  ) {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    modelCollection.modelExists((String) input.get("stock"),
      exists -> modelExists(completableFuture, exists));
    return completableFuture;
  }

  private void modelExists(
    CompletableFuture<Map<String, Object>> futureResponse, boolean exists
  ) {
    var response = Maps.<String, Object>newHashMap();
    response.put("exists", exists);
    futureResponse.complete(response);
  }

  @RequestMapping(path = "/model/find", method = RequestMethod.POST)
  public CompletableFuture<Map<String, Object>> findModel(
    @RequestBody Map<String, Object> input
  ) {
    var completableFuture = new CompletableFuture<Map<String, Object>>();
    modelCollection.findByStock((String) input.get("stock"),
      model -> findModel(completableFuture, model));
    return completableFuture;
  }

  private void findModel(
    CompletableFuture<Map<String, Object>> futureResponse, Model model
  ) {
    var response = Maps.<String, Object>newHashMap();
    response.put("buyModelPath", model.buyModelPath());
    response.put("sellModelPath", model.sellModelPath());
    response.put("reviewPeriod", model.reviewPeriod());
    response.put("buyTradePredictionMinimum", model.buyTradePredictionMinimum());
    response.put("sellTradePredictionMinimum", model.sellTradePredictionMinimum());
    futureResponse.complete(response);
  }
}
