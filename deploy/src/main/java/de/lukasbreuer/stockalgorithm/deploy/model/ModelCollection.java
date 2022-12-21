package de.lukasbreuer.stockalgorithm.deploy.model;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseCollection;
import de.lukasbreuer.stockalgorithm.core.database.DatabaseConnection;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class ModelCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "models";

  public static ModelCollection create(DatabaseConnection databaseConnection) {
    return new ModelCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME));
  }

  private ModelCollection(MongoCollection<Document> collection) {
    super(collection);
  }

  public void addModule(Model model, Consumer<InsertOneResult> response) {
    add(model.buildDocument(), response);
  }

  public void updateModule(Model model, Consumer<UpdateResult> response) {
    update(model.id(), model.buildDocument(), response);
  }

  public void removeModule(Model model, Consumer<DeleteResult> response) {
    removeModule(model.id(), response);
  }

  public void removeModule(UUID modelID, Consumer<DeleteResult> response) {
    remove(modelID, response);
  }

  public CompletableFuture<Model> findTradeById(UUID modelId) {
    return findById(modelId).thenApply(Model::of);
  }
}
