package de.lukasbreuer.cast.access.notification;

import com.google.common.collect.Maps;
import de.lukasbreuer.cast.deploy.notification.Device;
import de.lukasbreuer.cast.deploy.notification.DeviceCollection;
import de.lukasbreuer.cast.deploy.notification.NotificationConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeviceController {
  private final DeviceCollection deviceCollection;
  private final NotificationConfiguration notificationConfiguration;

  @RequestMapping(path = "/device/notification/information", method = RequestMethod.GET)
  public Map<String, Object> notificationInformation() {
    var response = Maps.<String, Object>newHashMap();
    response.put("apiKey", notificationConfiguration.apiKey());
    response.put("projectId", notificationConfiguration.projectId());
    response.put("messagingSenderId", notificationConfiguration.messagingSenderId());
    response.put("appId", notificationConfiguration.appId());
    return response;
  }

  @RequestMapping(path = "/device/register", method = RequestMethod.POST)
  public void registerDevice(@RequestBody Map<String, Object> input) {
    var token = (String) input.get("token");
    deviceCollection.deviceExists(token, exists -> registerDevice(token, exists));
  }

  private void registerDevice(String token, boolean exists) {
    if (!exists) {
      deviceCollection.addDevice(Device.create(UUID.randomUUID(), token),
        success -> {});
    }
  }
}
