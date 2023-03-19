package de.lukasbreuer.cast.core.yahoo;

import de.lukasbreuer.cast.core.configuration.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

@Accessors(fluent = true)
public final class YahooConfiguration extends Configuration {
  private static final String CONFIGURATION_PATH = "/configurations/yahoo/yahoo.json";

  public static YahooConfiguration createAndLoad() throws Exception {
    var configuration = new YahooConfiguration(CONFIGURATION_PATH);
    configuration.load();
    return configuration;
  }

  @Getter
  private String apiKey;

  private YahooConfiguration(String path) {
    super(path);
  }

  @Override
  protected void deserialize(JSONObject json) {
    apiKey = json.getString("apiKey");
  }
}
