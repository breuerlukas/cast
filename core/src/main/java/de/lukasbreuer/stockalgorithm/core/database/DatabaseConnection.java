package de.lukasbreuer.stockalgorithm.core.database;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.text.MessageFormat;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class DatabaseConnection {
  private final String hostname;
  private final int port;
  private final String databaseName;
  private MongoClient client;
  @Getter
  private MongoDatabase database;

  public void connect() {
    client = MongoClients.create(new ConnectionString(
      MessageFormat.format("mongodb://{0}:{1}", hostname, String.valueOf(port))));
    database = this.client.getDatabase(databaseName);
  }

  public void connect(String username, String password, String databaseName) {
    client = MongoClients.create(new ConnectionString(
      MessageFormat.format("mongodb://(0):(1)@(3):(4)/(5)",
        username, password, hostname, port, databaseName)));
    database = client.getDatabase(databaseName);
  }
}
