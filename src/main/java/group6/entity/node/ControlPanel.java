package group6.entity.node;

import group6.protocol.Message;
import group6.protocol.MessageType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Control panel node (domain model).
 * 
 * Responsibilities:
 * - Manages connections to multiple SensorNodes via SensorNodeClient.
 * - Recives messages from SensorNodes and maintains a data cache.
 * - Exposes methods to display node data, and send commands to actuators.
 *
 * @author Fidjor, dotDennis
 * @since 0.1.0
 */
public class ControlPanel extends Node {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanel.class);
  private final Map<String, SensorNodeClient> sensorClients;
  private final Map<String, NodeData> dataCache;
  private volatile boolean running;

  /**
   * Creates a control panel with the specified ID.
   *
   * @param nodeId unique identifier for this control panel.
   */
  public ControlPanel(String nodeId) {
    super(nodeId, NodeType.CONTROL);
    this.sensorClients = new ConcurrentHashMap<>();
    this.dataCache = new ConcurrentHashMap<>();
    this.running = true;
  }

  /**
   * Helper class to cache data from node
   */
  public static class NodeData {
    private final Map<String, Double> sensorReadings;
    private final Map<String, Boolean> actuatorStates;
    private long lastUpdate;

    public NodeData() {
      this.sensorReadings = new ConcurrentHashMap<>();
      this.actuatorStates = new ConcurrentHashMap<>();
      this.lastUpdate = System.currentTimeMillis();
    }

    public void updateSensor(String type, double value) {
      sensorReadings.put(type, value);
      lastUpdate = System.currentTimeMillis();
    }

    public void updateActuator(String type, boolean state) {
      actuatorStates.put(type, state);
      lastUpdate = System.currentTimeMillis();
    }

    public Map<String, Double> getSensorReadings() {
      return sensorReadings;
    }

    public Map<String, Boolean> getActuatorStates() {
      return actuatorStates;
    }

    public long getLastUpdate() {
      return lastUpdate;
    }
  }

  // --------- API ---------

  /**
   * Connects a sensornode to the specified address
   * Starts a listener thread to recieve messages from the node.
   *
   * @param sensorNodeId the ID of the node to connect to.
   * @param host         the host or IP address.
   * @param port         the portnumber
   */
  public void connectToSensorNode(String sensorNodeId, String host, int port) {
    if (sensorClients.containsKey(sensorNodeId)) {
      LOGGER.info("Node {} is already connected to {}:{}", sensorNodeId, host, port);
      return;
    }

    SensorNodeClient client = new SensorNodeClient(sensorNodeId, host, port, this);
    sensorClients.put(sensorNodeId, client);
    dataCache.put(sensorNodeId, new NodeData());
    client.start();

    LOGGER.info("Connecting to sensor node {} at {}:{}", sensorNodeId, host, port);

  }

  /**
   * Sends a command to control an actuator on a sensor node.
   *
   * @param sensorNodeId The ID of the sensor node.
   * @param actuatorType the type of actuator
   * @param state        true for ON false for OFF
   */
  public void sendCommand(String sensorNodeId, String actuatorType, boolean state) {
    SensorNodeClient client = sensorClients.get(sensorNodeId);
    if (client == null) {
      LOGGER.warn("Cannot send command. Not connected to sensor node {}", sensorNodeId);
      return;
    }
    client.sendCommand(actuatorType, state);
  }

  // TODO: Refactor to UI class later
  /**
   * Displays the cached data from a sensor node.
   *
   * @param sensorNodeId the ID of the sensor node
   */
  public void displayNodeData(String sensorNodeId) {
    NodeData data = dataCache.get(sensorNodeId);
    if (data == null) {
      System.out.println("[ControlPanel] >> No data available for node: " + sensorNodeId);
      return;
    }

    System.out.println("\n=== Node: " + sensorNodeId + " ===");

    // Display sensors
    Map<String, Double> sensors = data.getSensorReadings();
    if (!sensors.isEmpty()) {
      System.out.println("Sensors: ");
      for (Map.Entry<String, Double> entry : sensors.entrySet()) {
        System.out.printf("  %s: %.2f%n", entry.getKey(), entry.getValue());
      }
    }

    // Display actuators
    Map<String, Boolean> actuators = data.getActuatorStates();
    if (!actuators.isEmpty()) {
      System.out.println("Actuators: ");
      for (Map.Entry<String, Boolean> entry : actuators.entrySet()) {
        String state = entry.getValue() ? "ON" : "OFF";
        System.out.printf("  %s: %s%n", entry.getKey(), state);
      }
    }

    if (sensors.isEmpty() && actuators.isEmpty()) {
      System.out.println("[ControlPanel] >> No sensor or actuator data received yet.");
    }

    // Display last update time
    long secondsAgo = (System.currentTimeMillis() - data.getLastUpdate()) / 1000;
    System.out.println("Last update: " + secondsAgo + " seconds ago");
    System.out.println("=================\n");
  }

  // --------- Callbacks (Called by SensorNodeClient) ---------

  /**
   * Callback from SensorNodeClient when a message is recieved.
   * 
   * @param sensorNodeId the ID of the sensornode
   * @param msg          the recieved message
   */
  void handleIncomingMessage(String sensorNodeId, Message msg) {
    MessageType type = msg.getMessageType();
    switch (type) {
      case DATA -> {
        parseAndCacheData(sensorNodeId, msg.getData());
        LOGGER.debug("Received data from {}: {}", sensorNodeId, msg.getData());
      }
      case SUCCESS ->
        LOGGER.info("Command successful from {}: {}", sensorNodeId, msg.getData());
      case FAILURE ->
        LOGGER.warn("Command failed from {}: {}", sensorNodeId, msg.getData());
      case ERROR -> LOGGER.error("Error reported from {}: {}", sensorNodeId, msg.getData());
      default ->
        LOGGER.info("{} from {}, payload: {}", type, sensorNodeId, msg.getData());
    }
  }

  /**
   * Parses data string and updates the cache.
   * Data format: "key:value,key:value..."
   * Example: "temperature:15.5,humidity:40.9,heater:0"
   *
   * @param sensorNodeId The ID of the sensornode
   * @param data         the data string to parse.
   */
  private void parseAndCacheData(String sensorNodeId, String data) {
    if (data == null || data.isEmpty()) {
      return;
    }

    NodeData nodeData = dataCache.get(sensorNodeId);
    if (nodeData == null) {
      return;
    }

    String[] pairs = data.split(",");
    for (String pair : pairs) {
      String[] keyValue = pair.split(":");
      if (keyValue.length != 2) {
        LOGGER.warn("Invalid data format from {}: {}", sensorNodeId, pair);
        continue;
      }

      String key = keyValue[0].trim().toLowerCase();
      String value = keyValue[1].trim();

      // Check if it's an actuator (0 or 1) FIRST
      if (value.equals("0") || value.equals("1")) {
        boolean state = value.equals("1");
        nodeData.updateActuator(key, state);
      } else {
        // Otherwise treat as sensor
        try {
          double numericalValue = Double.parseDouble(value);
          nodeData.updateSensor(key, numericalValue);
        } catch (NumberFormatException e) {
          LOGGER.warn("Could not parse value {} for key: {} (node {})", value, key, sensorNodeId);
        }
      }
    }
  }

  /**
   * Returns cached data for a given sensor node.
   * Returns null if no such node exists yet.
   *
   * @param sensorNodeId the ID of the sensor node
   * @return NodeData or null if none
   */
  public NodeData getNodeData(String sensorNodeId) {
    return dataCache.get(sensorNodeId);
  }

  /**
   * Shuts down the control panel
   * Closes all connections and stops all listener threads
   */
  public void shutdown() {
    running = false;
    LOGGER.info("Shutting down control panel...");

    for (Map.Entry<String, SensorNodeClient> entry : sensorClients.entrySet()) {
      try {
        entry.getValue().stop();
        LOGGER.info("Stopped client for node {}", entry.getKey());
      } catch (Exception e) {
        LOGGER.error("Failed to stop client for node {}", entry.getKey(), e);
      }
    }
    sensorClients.clear();
    dataCache.clear();
    LOGGER.info("Shutdown complete.");
  }

  /**
   * Returns whether the control panel is running.
   * To be used by TUI or UI loops.
   */
  public boolean isRunning() {
    return running;
  }
}
