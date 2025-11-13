package group6.entity.node;

import group6.net.Connection;
import group6.protocol.Message;
import group6.protocol.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Control panel node that connects to sensor nodes.
 * Can display sensor data and send commands to actuators.
 *
 * @author Fidjor
 * @since 0.1.0
 */
public class ControlPanel extends Node{

  private final Map<String, Connection> connections;
  private final Map<String, NodeData> dataCache;
  private boolean running;

  /**
   * Creates a control panel with the specified ID.
   *
   * @param nodeId unique identifier for this control panel.
   */
  public ControlPanel (String nodeId) {
    super (nodeId, NodeType.CONTROL);
    this.connections = new ConcurrentHashMap<>();
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

  /**
   * Connects a sensornode to the specified address
   * Starts a listener thread to recieve messages from the node.
   *
   * @param sensorNodeId the ID of the node to connect to.
   * @param host the host or IP address.
   * @param port the portnumber
   * @throws IOException if connection fails
   */
  public void connectToSensorNode(String sensorNodeId, String host, int port) throws IOException {
    if (connections.containsKey(sensorNodeId)) {
      System.out.println("Node " + sensorNodeId + " is already connected to " + host + ":" + port);
      return;
    }

    Socket socket = new Socket(host, port);
    Connection connection = new Connection(socket);
    connections.put(sensorNodeId, connection);
    dataCache.put(sensorNodeId, new NodeData());

    System.out.println("Connected to sensor node" + sensorNodeId + "at" + host + ":" + port);

    Thread listenerThread = new Thread(() -> listenToSensorNode (sensorNodeId, connection));
    listenerThread.setName("Listener-" + sensorNodeId);
    listenerThread.start();
  }

  /**
   * Listens for incoming messages from sensor node.
   * Runs in its own thread and updates dataCache.
   *
   * @param sensorNodeId the ID of the sensornode
   * @param connection the connection to listen to.
   */
  private void listenToSensorNode (String sensorNodeId, Connection connection) {
    try {
      while (running && connection.isOpen()) {
        String message = connection.recvUtf();
        Message msg = Message.fromProtocolString(message);

        if(msg==null) {
          System.err.println("Invalid message received from " + sensorNodeId);
          continue;
        }

        handleMessage(sensorNodeId, msg);
      }
    }catch (IOException e) {
      System.err.println("Connection lost to sensor node: " + sensorNodeId);
    } finally {
      connections.remove(sensorNodeId);
      System.out.println("Disconnected from sensor node" + sensorNodeId);
    }
  }

  /**
   * Handles an incoming message from a sensor node.
   * Updates the data cache based on message type.
   *
   * @param sensorNodeId the ID of the sensornode
   * @param msg the recieved message
   */
  private void handleMessage (String sensorNodeId, Message msg) {
    MessageType type = msg.getMessageType();

    switch (type) {
      case DATA:
        parseAndCacheData(sensorNodeId, msg.getData());
        System.out.println("Recieved data from " + sensorNodeId + ": " + msg.getData());
        break;

      case SUCCESS:
        System.out.println("Command successful from " + sensorNodeId + ": " + msg.getData());
        break;

      case FAILURE:
        System.err.println("Command failed from " + sensorNodeId + ": " + msg.getData());
        break;

      case ERROR:
        System.err.println("Error from " + sensorNodeId + ": " + msg.getData());
        break;

      default:
        System.out.println("Recieved " + type + " from " + sensorNodeId);
        break;
    }
  }

  /**
   * Parses data string and updates the cache.
   * Data format: "key:value,key:value..."
   * Example: "temperature:15.5,humidity:40.9,heater:0"
   *
   * @param sensorNodeId The ID of the sensornode
   * @param data the data string to parse.
   */
  private void parseAndCacheData (String sensorNodeId, String data) {
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
        System.err.println("Invalid data format: " + pair);
        continue;
      }

      String key = keyValue[0].trim().toLowerCase();
      String value = keyValue[1].trim();

      try {
        // For now try to parse as sensor (double value)
        double numericalValue = Double.parseDouble(value);
        nodeData.updateSensor(key, numericalValue);
      }catch (NumberFormatException e) {
        // If not a number, try as actuator 0 or 1
        if(value.equals("0") || value.equals("1")) {
          boolean state = value.equals("1");
          nodeData.updateActuator(key, state);
        }else {
          System.err.println("Could not parse value " + value + " for key: " + key);
        }
      }
    }
  }

  /**
   * Sends a command to control an actuator on a sensor node.
   *
   * @param sensorNodeId The ID of the sensor node.
   * @param actuatorType the type of actuator
   * @param state true for ON false for OFF
   */
  public void sendCommand(String sensorNodeId, String actuatorType, boolean state) {
    Connection connection = connections.get(sensorNodeId);
    if (connection == null || !connection.isOpen()) {
      System.err.println("Not connected to sensornode :" + sensorNodeId);
      return;
    }

    String commandData = actuatorType.toLowerCase() + ":" + (state ? "1" : "0");
    Message command = new Message (MessageType.COMMAND, sensorNodeId, commandData);

    try {
      connection.sendUtf(command.toProtocolString());
      System.out.println("Sent command to " + sensorNodeId + ": " + commandData);
    }catch (IOException e) {
      System.err.println("Failed to send command to: " + sensorNodeId + ": " + e.getMessage());
    }
  }

  /**
   * Displays the cached data from a sensor node.
   *
   * @param sensorNodeId the ID of the sensor node
   */
  public void displayNodeData(String sensorNodeId) {
    NodeData data = dataCache.get(sensorNodeId);
    if (data == null) {
      System.out.println("No data available for node: " + sensorNodeId);
      return;
    }

    System.out.println("\n=== Node: " + sensorNodeId + " ===");

    //Display sensors
    Map<String, Double> sensors = data.getSensorReadings();
    if (!sensors.isEmpty()) {
      System.out.println("Sensors: ");
      for (Map.Entry<String, Double> entry : sensors.entrySet()) {
        System.out.printf("  %s: %.2f%n", entry.getKey(), entry.getValue());
      }
    }

    //Display actuators
    Map<String, Boolean> actuators = data.getActuatorStates();
    if (!actuators.isEmpty()) {
      System.out.println("Actuators: ");
      for (Map.Entry<String, Boolean> entry : actuators.entrySet()) {
        String state = entry.getValue() ? "ON" : "OFF";
        System.out.printf("  %s: %s%n", entry.getKey(), state);
      }
    }

    //Display last update time
    long secondsAgo = (System.currentTimeMillis() - data.getLastUpdate()) / 1000;
    System.out.println("Last update: " + secondsAgo + " seconds ago");
    System.out.println("=================\n");
  }

  /**
   * Shuts down the control panel
   * Closes all connections and stops all listener threads
   */
  public void shutdown() {
    running = false;
    System.out.println("Shutting down control panel");

    for (Map.Entry<String, Connection> entry : connections.entrySet()) {
      try {
        entry.getValue().close();
        System.out.println("Closed connection to: " + entry.getKey());
      }catch (IOException e) {
        System.err.println("Failed to close connection to: "
                + entry.getKey() + ": " + e.getMessage());
      }
    }

    connections.clear();
    dataCache.clear();
    System.out.println("Control panel shut down complete");
  }
}
