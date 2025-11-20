package group6.entity.device.sensor;

import group6.entity.device.SensorType;

/**
 * Humidity sensor implementation.
 */
public class HumiditySensor extends Sensor {

  /**
   * Creates a humidity sensor.
   *
   * @param deviceId e.g. "temp-01"
   */
  public HumiditySensor(String deviceId) {
    super(deviceId, SensorType.HUMIDITY, 30.0, 90.0);
  }

  /**
   * Simulates a humidity reading.
   * Uses random walk with a medium step to avoid jitter.
   */
  @Override
  public double readValue() {
    return randomWalk(1.5);
  }
}