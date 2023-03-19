package de.lukasbreuer.cast.core.yahoo;

import com.clearspring.analytics.util.Lists;
import de.lukasbreuer.cast.core.configuration.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.util.List;

@Accessors(fluent = true)
public final class YahooConfiguration extends Configuration {
  private static final String CONFIGURATION_PATH = "/configurations/yahoo/yahoo.json";

  public static YahooConfiguration createAndLoad() throws Exception {
    var configuration = new YahooConfiguration(CONFIGURATION_PATH);
    configuration.load();
    return configuration;
  }

  @Getter
  private List<String> apiKeys;

  private YahooConfiguration(String path) {
    super(path);
  }

  @Override
  protected void deserialize(JSONObject json) {
    apiKeys = Lists.newArrayList();
    var jsonApiKeys = json.getJSONArray("apiKeys");
    for (var i = 0; i < jsonApiKeys.length(); i++) {
      apiKeys.add(jsonApiKeys.getString(i));
    }
  }
}
