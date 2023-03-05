package de.lukasbreuer.cast.deploy.command;

import de.lukasbreuer.cast.core.command.Command;
import de.lukasbreuer.cast.core.log.Log;
import de.lukasbreuer.cast.deploy.notification.Device;
import de.lukasbreuer.cast.deploy.notification.DeviceCollection;
import de.lukasbreuer.cast.deploy.notification.Notification;
import de.lukasbreuer.cast.deploy.notification.NotificationFactory;

public final class NotificationCommand extends Command {
  public static NotificationCommand create(
    Log log, DeviceCollection deviceCollection,
    NotificationFactory notificationFactory
  ) {
    return new NotificationCommand(log, deviceCollection, notificationFactory);
  }

  private final DeviceCollection deviceCollection;
  private final NotificationFactory notificationFactory;

  private NotificationCommand(
    Log log, DeviceCollection deviceCollection,
    NotificationFactory notificationFactory
  ) {
    super(log, "notification", new String[0], new String[] {"title> <body> <icon"});
    this.deviceCollection = deviceCollection;
    this.notificationFactory = notificationFactory;
  }

  @Override
  public boolean execute(String[] arguments) {
    if (arguments.length != 3) {
      return false;
    }
    broadcastNotification(arguments);
    return true;
  }

  private void broadcastNotification(String[] arguments) {
    var title = arguments[0];
    var body = arguments[1];
    var icon = arguments[2];
    log().info("Notifications:");
    deviceCollection.allDevices(devices -> devices.forEach(device ->
      sendNotification(device, title, body, icon)));
  }

  private void sendNotification(
    Device device, String title, String body, String icon
  ) {
    var notification = notificationFactory.create(device, title, body, icon);
    try {
      var response = notification.send();
      var token = device.token().substring(0, 50);
      if (response == Notification.Status.SUCCESSFUL) {
        log().info(" - Successfully send to " + token + "...");
        return;
      }
      log().info(" - Failed to send to " + token + "... (will be removed from list)");
      deviceCollection.removeDevice(device, success -> {});
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
