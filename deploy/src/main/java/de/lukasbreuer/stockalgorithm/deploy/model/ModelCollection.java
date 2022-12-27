package de.lukasbreuer.stockalgorithm.deploy.model;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseCollection;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseConnection;
import org.bson.Document;

import java.util.UUID;
import java.util.function.Consumer;

public final class ModelCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "models";

  public static ModelCollection create(
    DatabaseConnection databaseConnection, ModelFactory modelFactory
  ) {
    return new ModelCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME), modelFactory);
  }

  private final ModelFactory modelFactory;

  private ModelCollection(
    MongoCollection<Document> collection, ModelFactory modelFactory
    ) {
    super(collection);
    this.modelFactory = modelFactory;
  }

  public void addModel(Model model, Consumer<InsertOneResult> response) {
    add(model.buildDocument(), response);
  }

  public void updateModel(Model model, Consumer<UpdateResult> response) {
    update(model.id(), model.buildDocument(), response);
  }

  public void removeModel(Model model, Consumer<DeleteResult> response) {
    removeModel(model.id(), response);
  }

  public void removeModel(UUID modelID, Consumer<DeleteResult> response) {
    remove(modelID, response);
  }

  public void findModelById(UUID modelId, Consumer<Model> result) {
    findById(modelId, document -> result.accept(modelFactory.of(document)));
  }

  public void findByStock(String stock, Consumer<Model> result) {
    findSingleByAttribute("stock", stock,
      document -> result.accept(modelFactory.of(document)));
  }
}
