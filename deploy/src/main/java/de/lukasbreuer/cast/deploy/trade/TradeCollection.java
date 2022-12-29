package de.lukasbreuer.cast.deploy.trade;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.lukasbreuer.cast.core.database.DatabaseCollection;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import de.lukasbreuer.cast.core.trade.TradeType;
import org.bson.Document;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class TradeCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "trades";

  public static TradeCollection create(DatabaseConnection databaseConnection) {
    return new TradeCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME));
  }

  private TradeCollection(MongoCollection<Document> collection) {
    super(collection);
  }

  public void addTrade(Trade trade, Consumer<InsertOneResult> response) {
    add(trade.buildDocument(), response);
  }

  public void updateTrade(Trade trade, Consumer<UpdateResult> response) {
    update(trade.id(), trade.buildDocument(), response);
  }

  public void removeTrade(Trade trade, Consumer<DeleteResult> response) {
    removeTrade(trade.id(), response);
  }

  public void removeTrade(UUID tradeId, Consumer<DeleteResult> response) {
    remove(tradeId, response);
  }

  public void findTradeById(UUID tradeID, Consumer<Trade> result) {
    findById(tradeID, document -> result.accept(Trade.of(document)));
  }

  public void findLatestByStock(
    String stock, TradeType tradeType, Consumer<Optional<Trade>> result
  ) {
    findByStock(stock, trades -> result.accept(trades.stream()
      .filter(trade -> trade.tradeType().equals(tradeType))
      .max(Comparator.comparing(Trade::tradeTime))));
  }

  public void findByStock(
    String stock, Consumer<List<Trade>> result
  ) {
    findMultipleByAttribute("stock", stock, documents ->
      result.accept(documents.stream().map(Trade::of).collect(Collectors.toList())));
  }
}
