package group6.protocol;

/**
 * Value object representing a sensor reading consisting of a
 * {@link DeviceKey} and a numerical value.
 */
public final class SensorReading {
  private final DeviceKey deviceKey;
  private final double value;

  private SensorReading(DeviceKey deviceKey, double value) {
    this.deviceKey = deviceKey;
    this.value = value;
  }

  public static SensorReading of(DeviceKey key, double value) {
    return new SensorReading(key, value);
  }

  public static SensorReading parse(String entry) {
    if (entry == null || entry.isBlank()) {
      throw new IllegalArgumentException("Entry cannot be blank");
    }
    String[] keyValue = entry.split(":");
    if (keyValue.length != 2) {
      throw new IllegalArgumentException("Invalid reading entry: " + entry);
    }
    DeviceKey key = DeviceKey.parse(keyValue[0]);
    double val = Double.parseDouble(keyValue[1].trim());
    return new SensorReading(key, val);
  }

  public DeviceKey getDeviceKey() {
    return deviceKey;
  }

  public double getValue() {
    return value;
  }

  public String toProtocolString() {
    return deviceKey.toProtocolKey() + ":" + value;
  }
}
