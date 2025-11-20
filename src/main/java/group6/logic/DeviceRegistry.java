package group6.logic;

import group6.entity.device.Device;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * Thread-safe registry for tracking devices associated with a SensorNode.
 * 
 * <p>Uses CopyOnWriteArrayList for safe concurrent access.
 * Methods to add, remove, snapshot, and find devices are provided.
 * 
 * @param <T> the device type
 */
public final class DeviceRegistry<T extends Device<?>> {

  private final CopyOnWriteArrayList<T> devices = new CopyOnWriteArrayList<>();

  /**
   * Adds a device to the registry.
   * 
   * @param device the device to add
   */
  public void add(T device) {
    devices.add(Objects.requireNonNull(device, "device"));
  }

  /**
   * Removes a device from the registry.
   * 
   * @param device the device to remove
   * @return true if the device was removed, false if it was not
   */
  public boolean remove(T device) {
    return devices.remove(device);
  }

  /**
   * Returns a snapshot list of all registered devices.
   * 
   * @return the list of devices
   */
  public List<T> snapshot() {
    return List.copyOf(devices);
  }

  /**
   * Finds the first device matching the given predicate.
   * 
   * @param predicate the predicate to match
   * @return the first matching device, or null if none
   */
  public T findFirst(Predicate<? super T> predicate) {
    for (T device : devices) {
      if (predicate.test(device)) {
        return device;
      }
    }
    return null;
  }
}
