package de.lukasbreuer.stockalgorithm.core.database;

import de.lukasbreuer.stockalgorithm.core.configuration.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.simple.JSONObject;

@Accessors(fluent = true)
public final class DatabaseConfiguration extends Configuration {
  public static DatabaseConfiguration createAndLoad() throws Exception {
    var configuration = new DatabaseConfiguration("/database/database.json");
    configuration.load();
    return configuration;
  }

  @Getter
  private String databaseName;

  private DatabaseConfiguration(String path) {
    super(path);
  }

  @Override
  protected void deserialize(JSONObject json) {
    databaseName = (String) json.get("databaseName");
  }
}
