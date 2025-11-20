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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Persists sensor readings to CSV files per node in cases of network outage.
 * <p>
 * CSV files are stored in the "history" directory, named by node ID.
 * Each file contains timestamped sensor readings for all sensors of that node.
 */
public final class SensorHistoryWriter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SensorHistoryWriter.class);
  private static final Path HISTORY_DIR = Paths.get("history");
  private static final DateTimeFormatter FOLDER_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm");
  private static final String RUN_FOLDER = LocalDateTime.now().format(FOLDER_FORMAT);
  private static final Map<String, Object> LOCKS = new ConcurrentHashMap<>();
  private static final Map<String, SensorSample> LAST_WRITTEN = new ConcurrentHashMap<>();

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
        String key = nodeId + "/" + sensorKey;
        SensorSample previous = LAST_WRITTEN.get(key);
        if (previous != null && (timestamp - previous.timestamp) < 900) { // 900 ms hardcoded limit
          return;
        }

        Path folder = HISTORY_DIR.resolve(RUN_FOLDER).resolve(nodeId);
        Files.createDirectories(folder);
        Path file = folder.resolve(sensorKey + ".csv");
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
          LAST_WRITTEN.put(key, new SensorSample(value, timestamp));
        }
      } catch (IOException e) {
        LOGGER.warn("Failed to write sensor history for {}", nodeId, e);
      }
    }
  }

  private static final class SensorSample {
    final double value;
    final long timestamp;

    SensorSample(double value, long timestamp) {
      this.value = value;
      this.timestamp = timestamp;
    }
  }
}
