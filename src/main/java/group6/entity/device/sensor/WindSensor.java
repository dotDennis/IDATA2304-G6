package group6.entity.device.sensor;

import group6.entity.device.SensorType;

/**
 * Wind sensor implementation.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public class WindSensor extends Sensor {

  /**
   * Creates a wind speed sensor.
   * 
   * @param deviceId e.g. "wind-01"
   */
  public WindSensor(String deviceId) {
    super(deviceId, SensorType.WIND_SPEED, 0.0, 25.0);
  }

  /**
   * Simulates a wind speed reading.
   * Uses random walk with a medium step to avoid jitter.
   */
  @Override
  public double readValue() {
    return randomWalk(1.0);
  }
}