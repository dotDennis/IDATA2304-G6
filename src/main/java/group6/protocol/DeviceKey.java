package group6.protocol;

import java.util.Locale;
import java.util.Objects;

/**
 * Canonical representation of protocol keys of the form {@code type#id}.
 */
public final class DeviceKey {

  private final String type;
  private final String id;

  private DeviceKey(String type, String id) {
    this.type = type;
    this.id = id;
  }

  public static DeviceKey of(String type, String id) {
    return new DeviceKey(normalize(type), normalize(id));
  }

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

  public String getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public String toProtocolKey() {
    return id == null || id.isBlank() ? type : type + "#" + id.trim();
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(type, id);
  }

  @Override
  public String toString() {
    return toProtocolKey();
  }
}
