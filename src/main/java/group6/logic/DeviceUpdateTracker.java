package group6.logic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the last update timestamp for devices and pending sensor updates.
 * 
 * <p>Used to monitor device activity and ensure sensor updates are processed.
 * Methods to record updates, retrieve timestamps, and manage pending sensors
 * are provided.
 */
public final class DeviceUpdateTracker {

  private final Map<String, Long> deviceTimestamps = new ConcurrentHashMap<>();
  private final Map<String, Boolean> pendingSensors = new ConcurrentHashMap<>();

  /**
   * Records a sensor update for the given device.
   * 
   * @param deviceId the device id
   */
  public void recordSensorUpdate(String deviceId) {
    deviceTimestamps.put(deviceId, System.currentTimeMillis());
    pendingSensors.put(deviceId, true);
  }

  /**
   * Records an actuator update for the given device.
   * 
   * @param deviceId the device id
   */
  public void recordActuatorUpdate(String deviceId) {
    deviceTimestamps.put(deviceId, System.currentTimeMillis());
  }

  /**
   * Gets the last update timestamp for the given device.
   * 
   * @param deviceId the device id
   * @return the last update timestamp, or 0
   */
  public long getTimestamp(String deviceId) {
    return deviceTimestamps.getOrDefault(deviceId, 0L);
  }

  /**
   * Removes tracking data for the given device.
   * 
   * @param deviceId the device id
   */
  public void removeDevice(String deviceId) {
    deviceTimestamps.remove(deviceId);
    pendingSensors.remove(deviceId);
  }

  /**
   * Checks if there are any devices with pending sensor updates.
   * 
   * @return true if there are pending sensors, false otherwise
   */
  public boolean hasPendingSensors() {
    return pendingSensors.containsValue(true);
  }

  /**
   * Consumes the pending sensor update for the given device.
   * 
   * @param deviceId the device id
   * @return true if there was a pending sensor update, false otherwise
   */
  public boolean consumePendingSensor(String deviceId) {
    return pendingSensors.remove(deviceId) != null;
  }
}
