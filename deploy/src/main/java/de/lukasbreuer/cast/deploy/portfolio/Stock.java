package de.lukasbreuer.cast.deploy.portfolio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Stock {
  public static Stock of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("stockName"));
  }

  private final UUID id;
  private final String stockName;

  public String formattedStockName() {
    return stockName.toUpperCase();
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("stockName", stockName);
    return document;
  }
}
