package group6.entity.node;

import group6.entity.device.Actuator;
import group6.protocol.Message;
import group6.protocol.MessageType;

import java.io.*;
import java.net.Socket;

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
  private PrintWriter out;
  private BufferedReader in;
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
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      running = true;

      System.out.println("Client handler connected for node " + sensorNode.getNodeId());

      Thread sensorThread = new Thread(this::sendSensorDataPeriodically);
      sensorThread.start();

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
    if (out != null) {
      out.println(message.toProtocolString());
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
      while (running && !socket.isClosed()) {
        String sensorData = sensorNode.getSensorDataString();

       Message message = new Message (MessageType.SENSOR_DATA, sensorNode.getNodeId(),sensorData);
        sendMessage(message);

        Thread.sleep(5000);
      }
      }catch (InterruptedException e) {
      System.out.println("Client handler error "+ e.getMessage());
   }
  }

/**
 * Listens for incoming commands from the control panel.
 */
 private void listenForCommands(){
   try {
     String line;
     while (running && (line = in.readLine()) != null){
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

    Message ack = new Message(MessageType.ACK, sensorNode.getNodeId(),
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
      if (in != null) in.close();
      if (out != null) out.close();
      if (socket != null) socket.close();
    }catch (IOException e) {
      System.err.println("Error when closing connection "+ e.getMessage());
    }
  }
}
