package group6.entity.device.sensor;

import group6.entity.device.SensorType;

/**
 * pH sensor implementation.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public class PhSensor extends Sensor {

  /**
   * Creates a pH sensor.
   * 
   * @param deviceId e.g. "ph-01"
   */
  public PhSensor(String deviceId) {
    super(deviceId, SensorType.PH, 5.5, 8.0);
  }

  /**
   * Simulates a pH reading.
   * Uses random walk with a very small step to avoid jitter.
   */
  @Override
  public double readValue() {
    return randomWalk(0.02);
  }
}