package de.lukasbreuer.cast.deploy.finance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class BankAccountAccess {
  public static BankAccountAccess of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getDouble("moneyAmount"),
      AccessType.valueOf(document.getString("accessType")),
      document.getDouble("currentBalance"));
  }

  public enum AccessType {
    DEPOSIT,
    DEBIT
  }

  private final UUID id;
  private final double moneyAmount;
  private final AccessType accessType;
  private final double currentBalance;

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("moneyAmount", moneyAmount);
    document.append("accessType", accessType.toString());
    document.append("currentBalance", currentBalance);
    return document;
  }
}
