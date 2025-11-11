package group6.entity.node;

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

      //TODO: Start threads for sending data periodically
      //TODO: Listen for incoming commands

      //for now connection is open
      while (running && !socket.isClosed()) {
        Thread.sleep(1000);
      }

    }catch (IOException | InterruptedException e) {
      System.err.println("Client handler error "+ e.getMessage());
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
   * cleans up resources
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
