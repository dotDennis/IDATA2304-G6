package group6.entity.node;

import java.io.*;
import java.net.*;

/**
 * TCP server for a SensorNode.
 * Listens for incoming connections from control panels.
 *
 * @author Fidjor
 * @since 0.1.0
 */

public class SensorNodeServer {

  private final SensorNode sensorNode;
  private final int port;
  private ServerSocket serverSocket;
  private boolean running;

  /**
   * Creates a server for the given sensor node.
   *
   * @param sensorNode the sensor node to serve
   * @param port the port to listen to (12345)
   */
  public SensorNodeServer(SensorNode sensorNode, int port) {
    this.sensorNode = sensorNode;
    this.port = port;
    this.running = false;
  }

  /**
   * Starts the TCP server.
   * Listens for incoming connections.
   */
  public void start() {
    try {
      serverSocket = new ServerSocket(port);
      running = true;
      System.out.println("Sensor Node " + sensorNode.getNodeId() + " listening on port " + port);

      while (running) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Control panel connected");

        ClientHandler handler = new ClientHandler(clientSocket, sensorNode);
        Thread clientThread = new Thread(handler);
        clientThread.start();
      }
    }catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }

  /**
   * Stops the TCP server.
   */
  public void stop() {
    running = false;
    try {
      if (serverSocket != null) {
        serverSocket.close();
      }
    }catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }
}
