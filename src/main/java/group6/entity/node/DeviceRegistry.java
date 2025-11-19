package group6.entity.node;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import group6.entity.device.Device;

/**
 * Thread-safe registry for tracking devices associated with a {@link SensorNode}.
 */
final class DeviceRegistry<Type extends Device<?>> {

  private final CopyOnWriteArrayList<Type> devices = new CopyOnWriteArrayList<>();

  void add(Type device) {
    devices.add(Objects.requireNonNull(device, "device"));
  }

  boolean remove(Type device) {
    return devices.remove(device);
  }

  List<Type> snapshot() {
    return List.copyOf(devices);
  }

  Type findFirst(Predicate<? super Type> predicate) {
    for (Type device : devices) {
      if (predicate.test(device)) {
        return device;
      }
    }
    return null;
  }
}
