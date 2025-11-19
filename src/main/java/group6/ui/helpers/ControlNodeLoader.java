package group6.ui.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

/**
 * Loader for {@link ControlNodeConfig} from JSON resources on the classpath.
 * <p>
 * Reads a list of {@link ControlNodeConfig.Entry} objects from JSON and
 * converts them into a {@link ControlNodeConfig}.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public final class ControlNodeLoader {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final CollectionType ENTRY_LIST_TYPE = MAPPER.getTypeFactory().constructCollectionType(
      List.class,
      ControlNodeConfig.Entry.class);

  private ControlNodeLoader() {
    // Utility class, no instances
  }

  /**
   * Loads a {@link ControlNodeConfig} from a JSON resource on the classpath.
   *
   * @param resourceName the name of the resource file (relative to classpath
   *                     root)
   * @return the parsed configuration
   * @throws IOException if the resource is missing or cannot be read
   */
  public static ControlNodeConfig loadFromResource(String resourceName) throws IOException {
    try (InputStream in = getRequiredResource(resourceName)) {
      return readEntries(in);
    }
  }

  // ---------- Private helpers ----------

  /**
   * Returns an input stream for the given resource or throws if not found.
   * 
   * @param name the resource name
   * @return the input stream
   * @throws IOException if the resource is missing
   */
  private static InputStream getRequiredResource(String name) throws IOException {
    InputStream in = ControlNodeLoader.class
        .getClassLoader()
        .getResourceAsStream(name);

    if (in == null) {
      throw new IOException("Missing resource: " + name);
    }
    return in;
  }

  /**
   * Reads JSON from the given stream into a {@link ControlNodeConfig}.
   * 
   * @param in the input stream
   * @return the parsed configuration
   * @throws IOException if reading or parsing fails
   */
  private static ControlNodeConfig readEntries(InputStream in) throws IOException {
    List<ControlNodeConfig.Entry> entries = MAPPER.readValue(in, ENTRY_LIST_TYPE);

    return ControlNodeConfig.fromEntries(entries);
  }
}