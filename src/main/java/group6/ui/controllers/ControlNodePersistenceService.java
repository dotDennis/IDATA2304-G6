package group6.ui.controllers;

import group6.ui.helpers.ControlNodeConfig;
import group6.ui.helpers.ControlNodeLoader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persistence wrapper for loading and saving {@link ControlNodeConfig}.
 * 
 * <p>Used by the UI layer to avoid dealing with file handling directly.
 * Delegates all serialization work to {@link ControlNodeLoader}.
 *
 * @author dotDennis
 * @since 0.2.0
 */
public class ControlNodePersistenceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControlNodePersistenceService.class);

  private final Path configPath;

  /**
   * Creates a new {@code ControlNodePersistenceService}.
   *
   * @param configPath the path to the configuration file
   */
  public ControlNodePersistenceService(Path configPath) {
    this.configPath = configPath;
  }

  /**
   * Loads a {@link ControlNodeConfig} from disk.
   *
   * @return the loaded config, or an empty one if loading fails
   */
  public ControlNodeConfig load() {
    try {
      return ControlNodeLoader.load(configPath);
    } catch (NoClassDefFoundError e) {
      LOGGER.warn("Jackson runtime not available. Skipping config load, using defaults.");
      return ControlNodeConfig.fromEntries(Collections.emptyList());
    } catch (IOException e) {
      LOGGER.warn("Failed to load control node config, starting fresh", e);
      return ControlNodeConfig.fromEntries(Collections.emptyList());
    }
  }

  /**
   * Saves a configuration to disk.
   * Can throw if the Jackson runtime is not available -
   * but this is caught and logged internally for this project.
   *
   * @param config the configuration to save
   */
  public void save(ControlNodeConfig config) {
    try {
      ControlNodeLoader.save(configPath, config);
      LOGGER.info("Saved configuration to {}", configPath.toAbsolutePath());
    } catch (NoClassDefFoundError e) {
      LOGGER.warn("Jackson runtime not available. Skipping config save.");
    } catch (IOException e) {
      LOGGER.warn("Failed to save configuration", e);
    }
  }

  /**
   * Saves a snapshot automatically, if the snapshot is not null.
   * Similar to {@link #save(ControlNodeConfig)}, but ignores null snapshots.
   *
   * @param snapshot the configuration to save
   */
  public void autoSave(ControlNodeConfig snapshot) {
    if (snapshot == null) {
      return;
    }

    try {
      ControlNodeLoader.save(configPath, snapshot);
      LOGGER.info("Auto-saved configuration to {}", configPath.toAbsolutePath());
    } catch (NoClassDefFoundError e) {
      LOGGER.warn("Jackson runtime not available. Skipping auto-save.");
    } catch (IOException e) {
      LOGGER.warn("Auto-save failed", e);
    }
  }
}
