package group6.protocol;

/**
 * Value object representing a sensor reading consisting of a
 * {@link DeviceKey} and a numerical value.
 * <p>
 * Used for sending and receiving sensor data within the protocol.
 */
public final class SensorReading {
  private final DeviceKey deviceKey;
  private final double value;

  /**
   * Private constructor.
   */
  private SensorReading(DeviceKey deviceKey, double value) {
    this.deviceKey = deviceKey;
    this.value = value;
  }

  /**
   * Creates a sensor reading.
   * <p>
   * The device key identifies the sensor, and the value is the reading.
   * 
   * @param key the device key
   * @param value the sensor value
   * @return the sensor reading
   */
  public static SensorReading of(DeviceKey key, double value) {
    return new SensorReading(key, value);
  }

  /**
   * Parses a sensor reading from a protocol string.
   * <p>
   * The expected format is {@code type#id:value}.
   * 
   * @param entry the protocol string
   * @return the sensor reading
   */
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

  // ------- Getters -------

  /**
   * Gets the device key.
   * 
   * @return the device key
   */
  public DeviceKey getDeviceKey() {
    return deviceKey;
  }

  /**
   * Gets the sensor value.
   * 
   * @return the sensor value
   */
  public double getValue() {
    return value;
  }

  /**
   * Converts to protocol string.
   * <p>
   * Format is {@code type#id:value}.
   * 
   * @return the protocol string
   */
  public String toProtocolString() {
    return deviceKey.toProtocolKey() + ":" + value;
  }
}
