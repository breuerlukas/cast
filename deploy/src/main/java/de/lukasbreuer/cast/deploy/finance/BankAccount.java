package de.lukasbreuer.cast.deploy.finance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Accessors(fluent = true)
@AllArgsConstructor(staticName = "create")
public final class BankAccount {
  public static BankAccount of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("name"), document.getDouble("money"));
  }

  @Getter
  private final UUID id;
  @Getter
  private final String name;
  @Getter
  private double money;

  public void deposit(double amount) {
    money += amount;
  }

  public void debit(double amount) {
    money -= amount;
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("name", name);
    document.append("money", money);
    return document;
  }
}
