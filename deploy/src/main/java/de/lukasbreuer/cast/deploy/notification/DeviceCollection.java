package de.lukasbreuer.cast.deploy.notification;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.lukasbreuer.cast.core.database.DatabaseCollection;
import de.lukasbreuer.cast.core.database.DatabaseConnection;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class DeviceCollection extends DatabaseCollection {
  private static final String COLLECTION_NAME = "devices";

  public static DeviceCollection create(DatabaseConnection databaseConnection) {
    return new DeviceCollection(
      databaseConnection.database().getCollection(COLLECTION_NAME));
  }

  private DeviceCollection(MongoCollection<Document> collection) {
    super(collection);
  }

  public void addDevice(Device device, Consumer<InsertOneResult> response) {
    add(device.buildDocument(), response);
  }

  public void removeDevice(Device device, Consumer<DeleteResult> response) {
    removeDevice(device.id(), response);
  }

  public void removeDevice(UUID deviceId, Consumer<DeleteResult> response) {
    remove(deviceId, response);
  }

  public void deviceExists(String token, Consumer<Boolean> result) {
    exists("token", token, result);
  }

  public void allDevices(Consumer<List<Device>> result) {
    findAll(documents -> result.accept(documents.stream().map(Device::of)
      .collect(Collectors.toList())));
  }
}
