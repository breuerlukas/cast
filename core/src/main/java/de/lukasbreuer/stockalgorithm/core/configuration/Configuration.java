package de.lukasbreuer.stockalgorithm.core.configuration;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public abstract class Configuration {
  private final String path;

  protected Configuration(String path) {
    this.path = path;
  }

  protected abstract void deserialize(JSONObject json);

  public void load() throws Exception  {
    deserialize((JSONObject) new JSONParser()
      .parse(System.getProperty("user.dir") + path));
  }
}
