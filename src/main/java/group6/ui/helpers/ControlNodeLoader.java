package group6.ui.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

/**
 * Loader for {@link ControlNodeConfig} from JSON resources on the classpath.
 * 
 * <p>Reads a list of {@link ControlNodeConfig.Entry} objects from JSON and
 * converts them into a {@link ControlNodeConfig}.
 */
public final class ControlNodeLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControlNodeLoader.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final CollectionType ENTRY_LIST_TYPE = 
      MAPPER.getTypeFactory().constructCollectionType(List.class, ControlNodeConfig.Entry.class);

  private ControlNodeLoader() {
    // Utility class, no instances
  }

  /**
   * Loads a {@link ControlNodeConfig} from the given file.
   * 
   * @param file the file to load from
   * @return the loaded configuration
   * @throws IOException if reading or parsing fails
   */
  public static ControlNodeConfig load(Path file) throws IOException {
    if (file != null && Files.exists(file)) {
      LOGGER.info("Loading control node config from {}", file.toAbsolutePath());
      try (InputStream in = Files.newInputStream(file)) {
        ControlNodeConfig config = readEntries(in);
        LOGGER.info("Loaded {} control node entries", config.getEntries().size());
        return config;
      }
    }
    LOGGER.info("No config file found, using empty configuration");
    return ControlNodeConfig.fromEntries(null);
  }

  /**
   * Saves the given {@link ControlNodeConfig} to the specified file as JSON.
   * 
   * @param file   the file to save to
   * @param config the configuration to save
   * @throws IOException if writing fails
   */
  public static void save(Path file, ControlNodeConfig config) throws IOException {
    if (file == null || config == null) {
      return;
    }
    if (file.getParent() != null) {
      Files.createDirectories(file.getParent());
    }
    LOGGER.info("Saving {} control node entries to {}",
         config.getEntries().size(), file.toAbsolutePath());
    try (OutputStream out = Files.newOutputStream(file)) {
      MAPPER.writerWithDefaultPrettyPrinter().writeValue(out, config.getEntries());
    }
    LOGGER.info("Configuration written successfully");
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
