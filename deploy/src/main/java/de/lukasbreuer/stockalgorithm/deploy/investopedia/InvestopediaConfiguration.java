package de.lukasbreuer.stockalgorithm.deploy.investopedia;


import de.lukasbreuer.stockalgorithm.core.configuration.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

@Accessors(fluent = true)
public final class InvestopediaConfiguration extends Configuration {
  private static final String CONFIGURATION_PATH = "/investopedia/investopedia.json";

  public static InvestopediaConfiguration createAndLoad() throws Exception {
    var configuration = new InvestopediaConfiguration(CONFIGURATION_PATH);
    configuration.load();
    return configuration;
  }

  @Getter
  private String username;
  @Getter
  private String password;
  @Getter
  private String game;

  private InvestopediaConfiguration(String path) {
    super(path);
  }

  @Override
  protected void deserialize(JSONObject json) {
    username = json.getString("username");
    password = json.getString("password");
    game = json.getString("game");
  }
}
