package group6.entity.node;

import group6.entity.device.Actuator;
import group6.protocol.Message;
import group6.protocol.MessageType;

import java.io.*;
import java.net.Socket;
import group6.net.Connection;

/**
 * Handles communication with connected control panel.
 * Runs in its own thread.
 *
 * @author Fidjor, dotDennis
 * @since 0.1.0
 */
public class ClientHandler implements Runnable {

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

      System.out.println("[ClientHandler] >> Connected for node " + sensorNode.getNodeId());

      Thread sensorThread = new Thread(this::sendSensorDataPeriodically, "sensor-data-" + sensorNode.getNodeId());
      sensorThread.start();

      Thread actuatorThread = new Thread(this::sendActuatorStatusPeriodically,
          "actuator-status-" + sensorNode.getNodeId());
      actuatorThread.start();

      // Loop
      listenForCommands();

    } catch (IOException e) {
      System.err
          .println("[ClientHandler] >> Connection error for node " + sensorNode.getNodeId() + ": " + e.getMessage());
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
      System.err.println("[ClientHandler] >> Cannot send, connection closed for node " + sensorNode.getNodeId());
      return;
    }
    try {
      connection.sendUtf(message.toProtocolString());
    } catch (IOException e) {
      System.err.println(
          "[ClientHandler] >> Error sending message for node " + sensorNode.getNodeId() + ": " + e.getMessage());
      running = false;
    }
  }

  /**
   * Closes connection.
   */
  public void stop() {
    running = false;
  }

  /**
   * Sends sensor data to control panel every 5 seconds.
   */
  private void sendSensorDataPeriodically() {
    try {
      while (running && connection.isOpen()) {
        String sensorData = sensorNode.getSensorDataString();

        Message message = new Message(MessageType.DATA, sensorNode.getNodeId(), sensorData);
        sendMessage(message);

        Thread.sleep(5000);
      }
    } catch (InterruptedException e) {
      System.out.println("[ClientHandler] >> Sensor data thread interrupted for node " + sensorNode.getNodeId() + ": "
          + e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Sends actuator status to control panel every 10 seconds.
   */
  private void sendActuatorStatusPeriodically() {
    try {
      while (running && connection.isOpen()) {
        String actuatorStatus = sensorNode.getActuatorStatusString();

        Message message = new Message(MessageType.DATA, sensorNode.getNodeId(), actuatorStatus);
        sendMessage(message);

        Thread.sleep(10000);
      }
    } catch (InterruptedException e) {
      System.out.println("[ClientHandler] >> Actuator status thread interrupted for node " + sensorNode.getNodeId()
          + ": " + e.getMessage());
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
    } catch (IOException e) {
      System.err.println(
          "[ClientHandler] >> Error reading command for node " + sensorNode.getNodeId() + ": " + e.getMessage());
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

    Actuator actuator = sensorNode.findActuatorByType(actuatorType);
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
   ** cleans up resources
   */
  private void cleanup() {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (IOException ignored) {
      System.out.println("[ClientHandler] >> Closed session for " + sensorNode.getNodeId());
    }
  }
}
