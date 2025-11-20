package group6.entity.device.sensor;

import group6.entity.device.SensorType;

/**
 * Light sensor implementation.
 *
 * @author dotDennis
 * @since 0.2.0
 */
public class LightSensor extends Sensor {

  /**
   * Creates a light sensor.
   *
   * @param deviceId e.g. "light-01"
   */
  public LightSensor(String deviceId) {
    super(deviceId, SensorType.LIGHT, 0.0, 1200.0);
  }

  /**
   * Simulates a light reading.
   * Uses random walk with a large step to avoid jitter.
   */
  @Override
  public double readValue() {
    return randomWalk(40.0);
  }
}