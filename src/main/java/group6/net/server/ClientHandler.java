package group6.net.server;

import group6.entity.device.actuator.Actuator;
import group6.entity.node.SensorNode;
import group6.logic.events.SensorNodeUpdateListener;
import group6.net.Connection;
import group6.protocol.Message;
import group6.protocol.MessageType;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles communication with connected control panel.
 * Runs in its own thread.
 *
 * @author Fidjor, dotDennis
 * @since 0.1.0
 */
public class ClientHandler implements Runnable, SensorNodeUpdateListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);
  private final Socket socket;
  private final SensorNode sensorNode;
  private Connection connection;
  private volatile boolean running;

  /**
   * Creates a handler for client connection.
   *
   * @param socket     the client socker
   * @param sensorNode the sensor node this handler belongs to
   */
  public ClientHandler(Socket socket, SensorNode sensorNode) {
    this.socket = socket;
    this.sensorNode = sensorNode;
    this.running = false;
  }

  @Override
  public void run() {
    try {
      connection = new Connection(socket);
      running = true;

      LOGGER.info("Control panel connected for node {}", sensorNode.getNodeId());

      sensorNode.addUpdateListener(this);

      Thread sensorThread = new Thread(this::sendSensorDataPeriodically, "sensor-data-" + sensorNode.getNodeId());
      sensorThread.start();

      Thread actuatorThread = new Thread(this::sendActuatorStatusPeriodically,
          "actuator-status-" + sensorNode.getNodeId());
      actuatorThread.start();

      // Loop
      listenForCommands();

    } catch (IOException e) {
      LOGGER.error("Connection error for node {}", sensorNode.getNodeId(), e);
    } finally {
      cleanup();
    }
  }

  /**
   * Sends a message to the control panel
   *
   * @param message the message to send
   *
   */
  public void sendMessage(Message message) {
    if (connection == null || !connection.isOpen()) {
      LOGGER.warn("Cannot send, connection closed for node {}", sensorNode.getNodeId());
      return;
    }
    try {
      connection.sendUtf(message.toProtocolString());
    } catch (IOException e) {
      LOGGER.error("Error sending message for node {}", sensorNode.getNodeId(), e);
      running = false;
    }
  }

  /**
   * Closes connection and stops handler. Used when the server shuts down.
   */
  public void stop() {
    running = false;
    sensorNode.removeUpdateListener(this);
    closeConnection();
  }

  /**
   * Sends sensor data to control panel according to the configured interval.
   * Default is every 5 seconds.
   */
  private void sendSensorDataPeriodically() {
    try {
      while (running && connection.isOpen()) {
        sendHeartbeat();
        Thread.sleep(sensorNode.getSensorNodeInterval());
      }
    } catch (InterruptedException e) {
      LOGGER.debug("Sensor data thread interrupted for node {}", sensorNode.getNodeId(), e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Sends actuator status to control panel every 10 seconds.
   */
  private void sendActuatorStatusPeriodically() {
    try {
      while (running && connection.isOpen()) {
        sendHeartbeat();
        Thread.sleep(10000);
      }
    } catch (InterruptedException e) {
      LOGGER.debug("Actuator status thread interrupted for node {}", sensorNode.getNodeId(), e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Listens for incoming commands from the control panel.
   */
  private void listenForCommands() {
    try {
      while (running && connection.isOpen()) {
        String line = connection.recvUtf();
        if (line == null || line.isEmpty()) {
          continue;
        }

        Message message = Message.fromProtocolString(line);

        if (message == null) {
          sendError("Invalid message received");
          continue;
        }

        if (message.getMessageType() == MessageType.COMMAND) {
          handleCommand(message.getData());
        }
      }
    } catch (EOFException | SocketException e) {
      LOGGER.info("Control panel disconnected from node {}", sensorNode.getNodeId());
    } catch (IOException e) {
      LOGGER.error("Error reading command for node {}", sensorNode.getNodeId(), e);
    } finally {
      running = false;
    }
  }

  /**
   * Handles a command from the control panel.
   * Format: "actuatorType:action" (fan:1)
   */
  private void handleCommand(String commandData) {
    if (commandData == null || commandData.isEmpty()) {
      sendError("Empty command");
      return;
    }
    String[] parts = commandData.split(":");
    if (parts.length != 2) {
      sendError("Invalid command format (expected actuator:action)");
      return;
    }

    String actuatorType = parts[0];
    String action = parts[1];

    if ("refresh".equalsIgnoreCase(action)) {
      handleRefreshCommand(actuatorType);
      return;
    }

    Actuator actuator = sensorNode.findActuatorByDeviceId(actuatorType);
    if (actuator == null) {
      actuator = sensorNode.findActuatorByType(actuatorType);
    }
    if (actuator == null) {
      sendError("Unknown actuator: " + actuatorType);
      return;
    }

    boolean newState = "1".equals(action);
    actuator.setState(newState);

    Message reply = new Message(MessageType.SUCCESS, sensorNode.getNodeId(),
        actuatorType + ":" + action);

    sendMessage(reply);
  }

  /**
   * Sends an error message to the control panel.
   */
  private void sendError(String errorMessage) {
    Message error = new Message(MessageType.ERROR, sensorNode.getNodeId(), errorMessage);

    sendMessage(error);
  }
 
  /**
   * Handles refresh command from control panel.
   * Used to request immediate data update.
   * Often used after adding/removing sensors/actuators.
   *
   * @param action the refresh target (sensors, actuators, all)
   */
  private void handleRefreshCommand(String action) {
    String normalized = action == null ? "" : action.trim().toLowerCase();
    boolean refreshSensors = normalized.isEmpty() || "all".equals(normalized) || "sensors".equals(normalized);
    boolean refreshActuators = normalized.isEmpty() || "all".equals(normalized) || "actuators".equals(normalized);

    if (!refreshSensors && !refreshActuators) {
      sendError("Unknown refresh target: " + action);
      return;
    }

    if (refreshSensors) {
      sendSensorSnapshot();
    }
    if (refreshActuators) {
      sendActuatorSnapshot();
    }

    Message reply = new Message(MessageType.SUCCESS, sensorNode.getNodeId(),
        "refresh:" + (normalized.isEmpty() ? "all" : normalized));
    sendMessage(reply);
  }

  /**
   * Sends a full snapshot of all sensor readings regardless of pending changes.
   */
  private void sendSensorSnapshot() {
    String snapshot = sensorNode.getSensorSnapshot();
    Message message = new Message(MessageType.DATA, sensorNode.getNodeId(), snapshot);
    sendMessage(message);
  }

  /**
   * Sends only sensors that have reported new readings since the last send (delta update).
   */
  private void sendSensorDelta() {
    String updates = sensorNode.drainPendingSensorUpdates();
    if (updates == null || updates.isEmpty()) {
      return;
    }
    Message message = new Message(MessageType.DATA, sensorNode.getNodeId(), updates);
    sendMessage(message);
  }

  /**
   * Sends a full snapshot of actuator states.
   */
  private void sendActuatorSnapshot() {
    String actuatorStatus = sensorNode.getActuatorSnapshot();
    Message message = new Message(MessageType.DATA, sensorNode.getNodeId(), actuatorStatus);
    sendMessage(message);
  }

  private void sendHeartbeat() {
    Message heartbeat = new Message(MessageType.DATA, sensorNode.getNodeId(), "");
    sendMessage(heartbeat);
  }

  /**
   ** cleans up resources
   */
  private void cleanup() {
    sensorNode.removeUpdateListener(this);
    closeConnection();
    LOGGER.info("Closed session for {}", sensorNode.getNodeId());
  }

  private void closeConnection() {
    try {
      if (connection != null) {
        connection.close();
      } else if (socket != null && !socket.isClosed()) {
        socket.close();
      }
    } catch (IOException e) {
      LOGGER.debug("Error while closing connection for {}", sensorNode.getNodeId(), e);
    }
  }

  @Override
  public void onSensorsUpdated(SensorNode node) {
    if (running && connection != null && connection.isOpen()) {
      sendSensorDelta();
    }
  }

  @Override
  public void onActuatorsUpdated(SensorNode node) {
    if (running && connection != null && connection.isOpen()) {
      sendActuatorSnapshot();
    }
  }
}
