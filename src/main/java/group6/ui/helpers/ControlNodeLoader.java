package group6.ui.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

  public static ControlNodeConfig load(Path file) throws IOException {
    if (file != null && Files.exists(file)) {
      try (InputStream in = Files.newInputStream(file)) {
        return readEntries(in);
      }
    }
    return ControlNodeConfig.fromEntries(null);
  }

  public static void save(Path file, ControlNodeConfig config) throws IOException {
    if (file == null || config == null) {
      return;
    }
    if (file.getParent() != null) {
      Files.createDirectories(file.getParent());
    }
    try (OutputStream out = Files.newOutputStream(file)) {
      MAPPER.writerWithDefaultPrettyPrinter().writeValue(out, config.getEntries());
    }
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
