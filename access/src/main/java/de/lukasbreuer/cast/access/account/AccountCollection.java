package de.lukasbreuer.cast.access.account;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.lukasbreuer.cast.core.database.DatabaseCollection;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import org.bson.Document;

import java.util.UUID;
import java.util.function.Consumer;

public final class AccountCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "accounts";

  public static AccountCollection create(DatabaseConnection databaseConnection) {
    return new AccountCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME));
  }

  private AccountCollection(MongoCollection<Document> collection) {
    super(collection);
  }

  public void addAccount(Account account, Consumer<InsertOneResult> response) {
    add(account.buildDocument(), response);
  }

  public void updateAccount(Account account, Consumer<UpdateResult> response) {
    update(account.id(), account.buildDocument(), response);
  }

  public void removeAccount(String username, Consumer<DeleteResult> response) {
    findAccountByName(username, account -> removeAccount(account, response));
  }

  public void removeAccount(Account account, Consumer<DeleteResult> response) {
    removeAccount(account.id(), response);
  }

  public void removeAccount(UUID accountId, Consumer<DeleteResult> response) {
    remove(accountId, response);
  }

  public void accountExists(String username, Consumer<Boolean> result) {
    exists("username", username, result);
  }

  public void findAccountById(UUID accountId, Consumer<Account> result) {
    findById(accountId, document -> result.accept(Account.of(document)));
  }

  public void findAccountByName(String username, Consumer<Account> result) {
    findSingleByAttribute("username", username, document ->
      result.accept(Account.of(document)));
  }
}