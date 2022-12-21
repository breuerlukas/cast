package de.lukasbreuer.stockalgorithm.deploy.trade;

import de.lukasbreuer.stockalgorithm.core.trade.TradeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Trade {
  public static Trade of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("stock"), TradeType.valueOf(document.getString("tradeType")),
      document.getInteger("tradeTime"), document.getInteger("tradePrice"));
  }

  private final UUID id;
  private final String stock;
  private final TradeType tradeType;
  private final long tradeTime;
  private final double tradePrice;

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("stock", stock);
    document.append("tradeType", tradeType.toString());
    document.append("tradeTime", tradeTime);
    document.append("tradePrice", tradePrice);
    return document;
  }
}
