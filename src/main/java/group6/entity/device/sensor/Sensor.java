package group6.entity.device.sensor;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import group6.entity.device.Device;
import group6.entity.device.SensorType;

/**
 * Base class for all sensors.
 * Holds common metadata + current value, min/max range and timestamp.
 *
 * Unit is taken from SensorType.getDefaultUnit().
 *
 * @author dotDennis, Fidjor
 * @since 0.1.0
 */
public abstract class Sensor extends Device<SensorType> {

  private static final long DEFAULT_INTERVAL_MS = 5000;

  private final double minValue;
  private final double maxValue;

  protected double currentValue;
  private LocalDateTime lastUpdated;
  private long updateIntervalMs = DEFAULT_INTERVAL_MS;

  /**
   * Base constructor for all sensors.
   *
   * @param deviceId unique id (e.g. "sensor-01-temp")
   * @param type     sensor type enum
   * @param minValue logical min value this sensor will produce
   * @param maxValue logical max value this sensor will produce
   */
  protected Sensor(String deviceId,
      SensorType type,
      double minValue,
      double maxValue) {
    super(deviceId, type);

    if (maxValue < minValue) {
      throw new IllegalArgumentException("maxValue cannot be less than minValue");
    }

    this.minValue = minValue;
    this.maxValue = maxValue;
    this.currentValue = Double.NaN;
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Subclasses implement their own simulation logic here.
   *
   * @return new sensor reading.
   */
  public abstract double readValue();

  /**
   * Helper for subclasses: bounded random-walk around current value.
   *
   * @param step absolute max step size per update (e.g. 0.3 °C per tick)
   */
  protected double randomWalk(double step) {
    ThreadLocalRandom rnd = ThreadLocalRandom.current();

    if (Double.isNaN(currentValue)) {
      // start in the middle of the allowed range
      currentValue = (minValue + maxValue) / 2.0;
    }

    double delta = rnd.nextDouble(-step, step);
    return applyDelta(delta);
  }

  /**
   * Allows external components (e.g., simulation/actuator effects)
   * to nudge the value up or down, respecting min/max bounds.
   *
   * @param delta amount to add to the current value (may be negative)
   * @return the updated value
   */
  public synchronized double manualAdjust(double delta) {
    if (Double.isNaN(currentValue)) {
      // initialize in the middle if not yet read
      currentValue = (minValue + maxValue) / 2.0;
    }
    return applyDelta(delta);
  }

  /**
   * Helper for randomWalk and applyDelta methods.
   * 
   * @param delta change to apply to current value
   * @return new current value after applying delta and bounding
   */
  private double applyDelta(double delta) {
    double next = currentValue + delta;

    if (next < minValue)
      next = minValue;
    if (next > maxValue)
      next = maxValue;

    currentValue = next;
    lastUpdated = LocalDateTime.now();
    notifyDeviceUpdated();
    return currentValue;
  }

  // ---------- Getters ----------
  public double getMinValue() {
    return minValue;
  }

  public double getMaxValue() {
    return maxValue;
  }

  public synchronized double getCurrentValue() {
    if (Double.isNaN(currentValue)) {
      currentValue = (minValue + maxValue) / 2.0;
      lastUpdated = LocalDateTime.now();
    }
    return currentValue;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public synchronized void setUpdateInterval(long intervalMs) {
    if (intervalMs <= 0) {
      intervalMs = DEFAULT_INTERVAL_MS;
    }
    this.updateIntervalMs = intervalMs;
  }

  public synchronized long getUpdateInterval() {
    return updateIntervalMs;
  }
}
