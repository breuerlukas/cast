package de.lukasbreuer.cast.deploy.portfolio;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.lukasbreuer.cast.core.database.DatabaseCollection;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class StockCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "stocks";

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

  public void updateStock(UUID stockId, String stockName, Consumer<UpdateResult> response) {
    findStockById(stockId, stock -> updateStock(stock, stockName, response));
  }

  public void updateStock(Stock stock, String stockName, Consumer<UpdateResult> response) {
    stock.stockName(stockName);
    updateStock(stock, response);
  }

  public void updateStock(Stock stock, Consumer<UpdateResult> response) {
    update(stock.id(), stock.buildDocument(), response);
  }

  public void removeStock(String stockName, Consumer<DeleteResult> response) {
    findStockByName(stockName, stock -> removeStock(stock, response));
  }

  public void removeStock(Stock stock, Consumer<DeleteResult> response) {
    removeStock(stock.id(), response);
  }

  public void removeStock(UUID stockId, Consumer<DeleteResult> response) {
    remove(stockId, response);
  }

  public void findStockById(UUID stockId, Consumer<Stock> result) {
    findById(stockId, document -> result.accept(Stock.of(document)));
  }

  public void findStockByName(String stockName, Consumer<Stock> result) {
    findSingleByAttribute("stockName", stockName, document ->
      result.accept(Stock.of(document)));
  }

  public void totalPortfolio(Consumer<List<Stock>> result) {
    findAll(documents -> result.accept(documents.stream().map(Stock::of)
      .collect(Collectors.toList())));
  }
}
