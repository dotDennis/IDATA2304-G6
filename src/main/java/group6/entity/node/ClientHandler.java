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
 * @author Fidjor
 * @since 0.1.0
 */
public class ClientHandler implements Runnable {

  private final Socket socket;
  private final SensorNode sensorNode;
  private Connection connection;
  private boolean running;

  /**
   * Creates a handler for client connection.
   *
   * @param socket the client socker
   * @param sensorNode the sensor node
   */
  public ClientHandler(Socket socket, SensorNode sensorNode) {
    this.socket = socket;
    this.sensorNode = sensorNode;
    this.running = false;
  }

  @Override
  public void run() {
    try{
      //input and output streams
      connection = new Connection(socket);
      running = true;

      System.out.println("Client handler connected for node " + sensorNode.getNodeId());

      Thread sensorThread = new Thread(this::sendSensorDataPeriodically);
      sensorThread.start();

      Thread actuatorThread = new Thread(this::sendActuatorStatusPeriodically);
      actuatorThread.start();

      listenForCommands();

    }catch (IOException e) {
      System.err.println("Connection error for node "+ sensorNode.getNodeId());
    }finally {
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
    if (connection != null && connection.isOpen()) {
        try {
            connection.sendUtf(message.toProtocolString());
        }catch (IOException e) {
            System.err.println("Error sending message "+ sensorNode.getNodeId());
        }
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

       Message message = new Message (MessageType.DATA, sensorNode.getNodeId(),sensorData);
        sendMessage(message);

        Thread.sleep(5000);
      }
      }catch (InterruptedException e) {
      System.out.println("Client handler error "+ e.getMessage());
   }
  }

  /**
   * Sends actuator status to control panel every 10 seconds.
   */
  private void sendActuatorStatusPeriodically() {
    try {
      while (running && connection.isOpen()) {
          String actuatorStatus = sensorNode.getActuatorStatusString();

          Message message = new Message(MessageType.DATA, sensorNode.getNodeId(),actuatorStatus);
          sendMessage(message);

          Thread.sleep(10000);
      }
    }catch (InterruptedException e) {
        System.out.println("Actuator status thread interrupted");
    }
  }



/**
 * Listens for incoming commands from the control panel.
 */
 private void listenForCommands(){
   try {
     while (running && connection.isOpen()){
       String line = connection.recvUtf();
       Message message = Message.fromProtocolString(line);

       if (message == null) {
         sendError("Invalid message received");
         continue;
       }
       if(MessageType.COMMAND.equals(message.getMessageType())){
         handleCommand(message.getData());
       }
     }
   } catch (IOException e) {
     System.err.println("Error reading command "+ e.getMessage());
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
      sendError("Invalid command format");
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

    Message ack = new Message(MessageType.SUCCESS, sensorNode.getNodeId(),
            actuatorType + ":" + action);

    sendMessage(ack);
  }

  /**
   * Sends an error message to the control panel.
   */
  private void sendError(String errorMessage) {
    Message error = new Message(MessageType.ERROR, sensorNode.getNodeId(),errorMessage);

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
    }catch (IOException e) {
      System.err.println("Error when closing connection "+ e.getMessage());
    }
  }
}
