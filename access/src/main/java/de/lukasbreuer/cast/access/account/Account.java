package de.lukasbreuer.cast.access.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Account {
  public static Account of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("username"), document.getString("password"));
  }

  private final UUID id;
  private final String username;
  private final String password;

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("username", username);
    document.append("password", password);
    return document;
  }
}
