package group6.ui.helpers;

import group6.protocol.DeviceKey;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * Presentation helper that formats device keys into human readable labels.
 * 
 * @see DeviceKey
 * 
 * @author Dennis
 * @since 0.2.0
 */
public final class DevicePresentation {

  private final DeviceKey key;

  private DevicePresentation(DeviceKey key) {
    this.key = key;
  }

  /**
   * Creates a new {@code DevicePresentation} from a raw protocol key string.
   * 
   * @param rawKey the raw protocol key string
   * @return the device presentation
   */
  public static DevicePresentation fromRawKey(String rawKey) {
    return new DevicePresentation(DeviceKey.parse(rawKey));
  }

  /**
   * Gets the base TYPE part of the key.
   * 
   * @return the base TYPE as string
   */
  public String getBaseType() {
    return key.getType();
  }

  /**
   * Gets the device ID part of the key.
   * 
   * @return the device ID as string
   */
  public String getDeviceId() {
    return key.getId();
  }

  /**
   * Formats a display name for the device with an optional icon.
   * 
   * @param icon the icon string to prepend, may be null or blank
   * @return the formatted display name
   */
  public String formatDisplayName(String icon) {
    StringBuilder builder = new StringBuilder();
    if (icon != null && !icon.isBlank()) {
      builder.append(icon).append(" ");
    }
    builder.append(capitalizeWords(key.getType()));
    if (key.getId() != null && !key.getId().isBlank()) {
      builder.append(" (").append(key.getId()).append(")");
    }
    return builder.toString();
  }

  private static String capitalizeWords(String text) {
    if (text == null || text.isBlank()) {
      return "";
    }
    String normalized = text.replace('-', ' ').replace('_', ' ');
    String[] parts = normalized.split("\\s+");
    StringJoiner joiner = new StringJoiner(" ");
    Arrays.stream(parts)
        .filter(part -> !part.isBlank())
        .forEach(part -> joiner.add(capitalize(part)));
    return joiner.toString().trim();
  }

  private static String capitalize(String part) {
    if (part.isEmpty()) {
      return part;
    }
    return part.substring(0, 1).toUpperCase(Locale.ROOT) 
        + part.substring(1).toLowerCase(Locale.ROOT);
  }
}
