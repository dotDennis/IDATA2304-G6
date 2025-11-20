package group6.logic;

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
   * Records a sensor sample to history.
   * <p>
   * If a sample for the same sensor was recorded less than 900 ms ago, it is skipped
   * to avoid excessive writes.
   * 
   * @param nodeId the sensor node id
   * @param sensorKey the sensor key
   * @param value   the sensor value
   * @param timestamp the sample timestamp
   */
  public static void recordSample(String nodeId, String sensorKey, double value, long timestamp) {
    Object lock = LOCKS.computeIfAbsent(nodeId, k -> new Object());
    synchronized (lock) {
      try {
        String cacheKey = nodeId + "/" + sensorKey;
        SensorSample previous = LAST_WRITTEN.get(cacheKey);
        if (previous != null && (timestamp - previous.timestamp) < 900) {
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
          LAST_WRITTEN.put(cacheKey, new SensorSample(value, timestamp));
        }
      } catch (IOException e) {
        LOGGER.warn("Failed to write sensor history for {}", nodeId, e);
      }
    }
  }

  // ------- Helper class -------

  /**
   * Represents a sensor sample with value and timestamp.
   */
  private static final class SensorSample {
    final double value;
    final long timestamp;

    SensorSample(double value, long timestamp) {
      this.value = value;
      this.timestamp = timestamp;
    }
  }
}
