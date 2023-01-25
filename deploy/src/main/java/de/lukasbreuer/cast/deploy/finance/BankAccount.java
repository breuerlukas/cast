package de.lukasbreuer.cast.deploy.finance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "create")
public final class BankAccount {
  public static BankAccount of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("name"), document.getDouble("money"),
      document.getList("accesses", Document.class).stream()
        .map(BankAccountAccess::of).collect(Collectors.toList()));
  }

  private final UUID id;
  private final String name;
  private double balance;
  private final List<BankAccountAccess> accesses;

  public void deposit(double amount) {
    balance += amount;
    accesses.add(BankAccountAccess.create(UUID.randomUUID(), amount,
      BankAccountAccess.AccessType.DEPOSIT, balance));
  }

  public void debit(double amount) {
    balance -= amount;
    accesses.add(BankAccountAccess.create(UUID.randomUUID(), amount,
      BankAccountAccess.AccessType.DEBIT, balance));
  }

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("name", name);
    document.append("balance", balance);
    document.append("accesses", accesses.stream()
      .map(BankAccountAccess::buildDocument)
      .collect(Collectors.toList()));
    return document;
  }
}
