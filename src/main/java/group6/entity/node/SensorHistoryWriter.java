package group6.entity.node;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persists sensor readings to CSV files per node in cases of network outage.
 * <p>
 * CSV files are stored in the "history" directory, named by node ID.
 * Each file contains timestamped sensor readings for all sensors of that node.
 *
 * //TODO: Sort by date.
 * 
 */
public final class SensorHistoryWriter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SensorHistoryWriter.class);
  private static final Path HISTORY_DIR = Paths.get("history");
  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();

  private SensorHistoryWriter() {
  }

  /**
   * Records a sensor sample to file for the given node.
   * 
   * @param nodeId    the node id
   * @param sensorKey the sensor key
   * @param value     the sensor value
   * @param timestamp the timestamp of the sample
   */
  public static void recordSample(String nodeId, String sensorKey, double value, long timestamp) {
    Object lock = LOCKS.computeIfAbsent(nodeId, k -> new Object());
    synchronized (lock) {
      try {
        Files.createDirectories(HISTORY_DIR);
        Path file = HISTORY_DIR.resolve(nodeId + ".csv");
        boolean newFile = Files.notExists(file);
        try (BufferedWriter writer = Files.newBufferedWriter(file,
            StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
          if (newFile) {
            writer.write("timestamp,sensor,value");
            writer.newLine();
          }
          LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
          writer.write(String.format("%s,%s,%s", time, sensorKey, value));
          writer.newLine();
        }
      } catch (IOException e) {
        LOGGER.warn("Failed to write sensor history for {}", nodeId, e);
      }
    }
  }
}
