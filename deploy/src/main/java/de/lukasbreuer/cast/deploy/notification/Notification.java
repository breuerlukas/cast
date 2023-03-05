package de.lukasbreuer.cast.deploy.notification;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

@RequiredArgsConstructor(staticName = "create")
public final class Notification {
  private final String serverKey;
  private final Device device;
  private final String title;
  private final String body;
  private final String icon;

  public enum Status {
    SUCCESSFUL,
    FAILED
  }

  public Status send() throws Exception {
    var response = new JSONObject(requestNotification());
    return response.getInt("success") > 0 ? Status.SUCCESSFUL : Status.FAILED;
  }

  private static final String FIREBASE_FCM_URL =
    "https://fcm.googleapis.com/fcm/send";

  private String requestNotification() throws Exception {
    var client = HttpClientBuilder.create().build();
    var request = new HttpPost(FIREBASE_FCM_URL);
    request.addHeader("Content-Type", "application/json");
    request.setHeader("Authorization", "key=" + serverKey);
    request.setEntity(new StringEntity(buildRequestBody()));
    var result = client.execute(request);
    return EntityUtils.toString(result.getEntity(), "UTF-8");
  }

  private String buildRequestBody() {
    var content = new JSONObject();
    content.put("to", device.token());
    var notification = new JSONObject();
    notification.put("title", title);
    notification.put("body", body);
    notification.put("icon", icon);
    content.put("notification", notification);
    return content.toString();
  }
}
