package group6.protocol;

import java.util.Locale;
import java.util.Objects;

/**
 * Representation of protocol keys of the form {@code type#id}.
 * 
 * <p>Used for identifying devices effectively within the protocol.
 * Instead of looping through all devices.
 */
public final class DeviceKey {

  private final String type;
  private final String id;

  private DeviceKey(String type, String id) {
    this.type = type;
    this.id = id;
  }

  /**
   * Creates a DeviceKey from type and id.
   * 
   * @param type the device type
   * @param id   the device id
   * @return the device key
   */
  public static DeviceKey of(String type, String id) {
    return new DeviceKey(normalize(type), normalize(id));
  }

  /**
   * Parses a protocol key string into a DeviceKey.
   * 
   * @param rawKey the raw protocol key string
   * @return the device key
   */
  public static DeviceKey parse(String rawKey) {
    if (rawKey == null || rawKey.isBlank()) {
      return new DeviceKey("", "");
    }
    String trimmed = rawKey.trim();
    int hashIndex = trimmed.indexOf('#');
    if (hashIndex < 0) {
      return new DeviceKey(normalize(trimmed), "");
    }
    String type = trimmed.substring(0, hashIndex);
    String id = trimmed.substring(hashIndex + 1);
    return new DeviceKey(normalize(type), id.trim());
  }

  private static String normalize(String text) {
    if (text == null) {
      return "";
    }
    return text.trim().toLowerCase(Locale.ROOT);
  }

  // ------- Getters -------

  /**
   * Gets the device type.
   * 
   * @return the device type
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the device id.
   * 
   * @return the device id
   */
  public String getId() {
    return id;
  }

  /**
   * Converts to protocol key string.
   * 
   * @return the protocol key string
   */
  public String toProtocolKey() {
    return id == null || id.isBlank() ? type : type + "#" + id.trim();
  }

  // ------- Overrides -------

  /**
   * Checks equality with another object.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeviceKey)) {
      return false;
    }
    DeviceKey other = (DeviceKey) o;
    return type.equals(other.type) && Objects.equals(id, other.id);
  }

  /**
   * Generates hash code.
   */
  @Override
  public int hashCode() {
    return Objects.hash(type, id);
  }

  /**
   * Converts to string representation.
   */
  @Override
  public String toString() {
    return toProtocolKey();
  }
}
