package de.lukasbreuer.cast.deploy.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "create")
public final class Device {
  public static Device of(Document document) {
    return create(UUID.fromString(document.getString("id")),
      document.getString("token"));
  }

  private final UUID id;
  private final String token;

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    document.append("token", token);
    return document;
  }
}
