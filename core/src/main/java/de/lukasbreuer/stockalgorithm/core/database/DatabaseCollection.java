package de.lukasbreuer.stockalgorithm.core.database;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DatabaseCollection {
  private final MongoCollection<Document> collection;

  protected void add(Document document, Consumer<InsertOneResult> response) {
    collection.insertOne(document)
      .subscribe(SingleSubscriber.of(response::accept));
  }

  protected void update(UUID id, Document document, Consumer<UpdateResult> response) {
    collection.replaceOne(Filters.eq("id", id.toString()), document)
      .subscribe(SingleSubscriber.of(response::accept));
  }

  protected void remove(UUID id, Consumer<DeleteResult> response) {
    collection.deleteOne(new Document("id", id.toString()))
      .subscribe(SingleSubscriber.of(response::accept));;
  }

  protected CompletableFuture<Document> findById(UUID id) {
    var completableFuture = new CompletableFuture<Document>();
    collection.find(Filters.eq("id", id.toString()))
      .subscribe(SingleSubscriber.of(completableFuture::complete));
    return completableFuture;
  }

  protected CompletableFuture<List<Document>> findAll() {
    var completableFuture = new CompletableFuture<List<Document>>();
    collection.find().subscribe(FullSubscriber.of(completableFuture::complete));
    return completableFuture;
  }
}