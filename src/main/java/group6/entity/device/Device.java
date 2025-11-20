package group6.entity.device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Abstract base class for all devices in the greenhouse system.
 * 
 * <p>Each device has a unique ID and a specific type defined by an enum.
 * This class also supports listeners to notify when the device state
 * changes.
 *
 * @param <T> Enum representing the type of the device
 */
public abstract class Device<T extends Enum<T>> {
  private final String deviceId;
  private final T deviceType;
  private final transient List<DeviceUpdateListener> updateListeners = new CopyOnWriteArrayList<>();

  /**
   * Constructs a Device with the specified ID and type.
   * 
   * @param deviceId   unique identifier for the device
   * @param deviceType type of the device
   * @throws IllegalArgumentException if deviceId is null/blank or deviceType is
   *                                  null
   */
  protected Device(String deviceId, T deviceType) {
    this.deviceId = validateDeviceId(deviceId);
    this.deviceType = validateDeviceType(deviceType);
  }

  // ---------- Helpers ----------

  /**
   * Validates the device ID string.
   * 
   * @param deviceId unique identifier for the device
   * @return the validated device ID
   * @throws IllegalArgumentException if deviceId is null or blank
   */
  protected static String validateDeviceId(String deviceId) {
    if (deviceId == null || deviceId.isBlank()) {
      throw new IllegalArgumentException("deviceId cannot be null or blank");
    }
    return deviceId.trim();
  }

  /**
   * Validates the device type enum.
   * 
   * @param <T> Type of the enum
   * @param deviceType type of the device
   * @return the validated device type
   * @throws IllegalArgumentException if deviceType is null
   */
  protected static <T extends Enum<T>> T validateDeviceType(T deviceType) {
    if (deviceType == null) {
      throw new IllegalArgumentException("deviceType cannot be null");
    }
    return deviceType;
  }

  // ---------- Getters ----------
  public String getDeviceId() {
    return deviceId;
  }

  public T getDeviceType() {
    return deviceType;
  }

  public String getDeviceTypeName() {
    return deviceType.name();
  }

  /**
   * Adds a listener to be notified when the device is updated.
   * 
   * @param listener the listener to add
   */
  public void addUpdateListener(DeviceUpdateListener listener) {
    if (listener != null) {
      updateListeners.add(listener);
    }
  }

  /**
   * Removes a listener from the update notifications.
   * 
   * @param listener the listener to remove
   */
  public void removeUpdateListener(DeviceUpdateListener listener) {
    updateListeners.remove(listener);
  }

  /**
   * Notifies all registered listeners that the device has been updated.
   */
  protected void notifyDeviceUpdated() {
    for (DeviceUpdateListener listener : updateListeners) {
      listener.onDeviceUpdated(this);
    }
  }
}
