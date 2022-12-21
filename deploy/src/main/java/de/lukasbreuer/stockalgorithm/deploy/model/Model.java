package de.lukasbreuer.stockalgorithm.deploy.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.Document;

import java.util.UUID;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public final class Model {
  public static Model of(Document document) {
    return create(UUID.fromString(document.getString("id")));
  }

  @Getter
  private final UUID id;

  public Document buildDocument() {
    var document = new Document("id", id.toString());
    return document;
  }
}
