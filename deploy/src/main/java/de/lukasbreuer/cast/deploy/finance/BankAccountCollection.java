package de.lukasbreuer.cast.deploy.finance;

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

public final class BankAccountCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "bank_accounts";

  public static BankAccountCollection create(DatabaseConnection databaseConnection) {
    return new BankAccountCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME));
  }

  private BankAccountCollection(MongoCollection<Document> collection) {
    super(collection);
  }

  public void addBankAccount(BankAccount account, Consumer<InsertOneResult> response) {
    add(account.buildDocument(), response);
  }

  public void updateBankAccount(BankAccount account, Consumer<UpdateResult> response) {
    update(account.id(), account.buildDocument(), response);
  }

  public void removeBankAccount(BankAccount account, Consumer<DeleteResult> response) {
    removeBankAccount(account.id(), response);
  }

  public void removeBankAccount(String accountName, Consumer<DeleteResult> response) {
    findBankAccountByName(accountName, account -> removeBankAccount(account, response));
  }

  public void removeBankAccount(UUID accountId, Consumer<DeleteResult> response) {
    remove(accountId, response);
  }

  public void findBankAccountById(UUID accountId, Consumer<BankAccount> result) {
    findById(accountId, document -> result.accept(BankAccount.of(document)));
  }

  public void findBankAccountByName(String accountName, Consumer<BankAccount> result) {
    findSingleByAttribute("name", accountName, document ->
      result.accept(BankAccount.of(document)));
  }

  public void firstBankAccount(Consumer<BankAccount> result) {
    allBankAccounts(accounts -> result.accept(accounts.get(0)));
  }

  public void allBankAccounts(Consumer<List<BankAccount>> result) {
    findAll(documents -> result.accept(documents.stream().map(BankAccount::of)
      .collect(Collectors.toList())));
  }
}
