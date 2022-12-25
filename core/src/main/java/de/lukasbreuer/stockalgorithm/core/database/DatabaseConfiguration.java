package de.lukasbreuer.stockalgorithm.core.database;

import de.lukasbreuer.stockalgorithm.core.configuration.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

@Accessors(fluent = true)
public final class DatabaseConfiguration extends Configuration {
  private static final String CONFIGURATION_PATH = "/configuration/database/database.json";

  public static DatabaseConfiguration createAndLoad() throws Exception {
    var configuration = new DatabaseConfiguration(CONFIGURATION_PATH);
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
    databaseName = json.getString("databaseName");
  }
}
