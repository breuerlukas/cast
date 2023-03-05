package de.lukasbreuer.cast.deploy.notification;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__({@Inject}))
public final class NotificationFactory {
  @Inject
  @Named("notificationServerKey")
  private final String serverKey;

  public Notification create(
    Device device, String title, String body, String icon
  ) {
    return Notification.create(serverKey, device, title, body, icon);
  }
}
