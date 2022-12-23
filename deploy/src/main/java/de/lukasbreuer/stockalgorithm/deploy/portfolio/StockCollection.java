package de.lukasbreuer.stockalgorithm.deploy.portfolio;

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

public final class StockCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "portfolio";

  public static StockCollection create(DatabaseConnection databaseConnection) {
    return new StockCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME));
  }

  private StockCollection(MongoCollection<Document> collection) {
    super(collection);
  }

  public void addStock(Stock stock, Consumer<InsertOneResult> response) {
    add(stock.buildDocument(), response);
  }

  public void updateStock(Stock stock, Consumer<UpdateResult> response) {
    update(stock.id(), stock.buildDocument(), response);
  }

  public void removeStock(Stock stock, Consumer<DeleteResult> response) {
    removeStock(stock.id(), response);
  }

  public void removeStock(UUID stockId, Consumer<DeleteResult> response) {
    remove(stockId, response);
  }

  public CompletableFuture<Stock> findStockById(UUID stockId) {
    return findById(stockId).thenApply(Stock::of);
  }
}
