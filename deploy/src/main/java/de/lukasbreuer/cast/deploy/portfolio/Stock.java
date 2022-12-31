package de.lukasbreuer.cast.deploy.portfolio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "create")
public final class Stock {
  public static Stock of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("stockName"));
  }

  private final UUID id;
  @Setter
  private String stockName;

  public String formattedStockName() {
    return stockName.toUpperCase();
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("stockName", stockName);
    return document;
  }
}
