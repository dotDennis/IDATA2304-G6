package group6.entity.node;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks update timestamps for devices and pending sensor updates.
 */
final class DeviceUpdateTracker {

  private final Map<String, Long> timestamps = new ConcurrentHashMap<>();
  private final Set<String> pendingSensorIds = ConcurrentHashMap.newKeySet();

  void recordSensorUpdate(String deviceId) {
    String normalized = normalize(deviceId);
    timestamps.put(deviceId, System.currentTimeMillis());
    pendingSensorIds.add(normalized);
  }

  void recordActuatorUpdate(String deviceId) {
    timestamps.put(deviceId, System.currentTimeMillis());
  }

  void removeDevice(String deviceId) {
    timestamps.remove(deviceId);
    pendingSensorIds.remove(normalize(deviceId));
  }

  long getTimestamp(String deviceId) {
    return timestamps.getOrDefault(deviceId, 0L);
  }

  boolean hasPendingSensors() {
    return !pendingSensorIds.isEmpty();
  }

  boolean consumePendingSensor(String normalizedId) {
    return pendingSensorIds.remove(normalizedId);
  }

  private static String normalize(String id) {
    if (id == null) {
      return "";
    }
    return id.trim().toLowerCase(Locale.ROOT);
  }
}
