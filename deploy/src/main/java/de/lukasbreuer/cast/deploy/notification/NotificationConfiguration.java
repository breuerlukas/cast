package de.lukasbreuer.cast.deploy.notification;

import de.lukasbreuer.cast.core.configuration.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

@Accessors(fluent = true)
public final class NotificationConfiguration extends Configuration {
  private static final String CONFIGURATION_PATH = "/configurations/notification/notification.json";

  public static NotificationConfiguration createAndLoad() throws Exception {
    var configuration = new NotificationConfiguration(CONFIGURATION_PATH);
    configuration.load();
    return configuration;
  }

  @Getter
  private String apiKey;
  @Getter
  private String projectId;
  @Getter
  private String messagingSenderId;
  @Getter
  private String appId;
  @Getter
  private String severKey;

  private NotificationConfiguration(String path) {
    super(path);
  }

  @Override
  protected void deserialize(JSONObject json) {
    apiKey = json.getString("apiKey");
    projectId = json.getString("projectId");
    messagingSenderId = json.getString("messagingSenderId");
    appId = json.getString("appId");
    severKey = json.getString("serverKey");
  }
}