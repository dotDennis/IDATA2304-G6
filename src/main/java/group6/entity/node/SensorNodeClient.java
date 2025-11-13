package group6.entity.node;

import java.io.IOException;

import group6.net.Connection;
import group6.net.TcpClient;
import group6.protocol.Message;
import group6.protocol.MessageType;
import group6.ui.helpers.VersionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Per-sensor client running on the control-panel side.
 * - Uses TcpClient and Connection to talk to one SensorNode.
 * - Runs a receive loop in its own thread.
 * - Forwards parsed messages back to ControlPanel.
 * 
 * @author dotDennis
 * @since 0.1.0
 */
public class SensorNodeClient implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionUtil.class);
    private final String sensorNodeId;
    private final String host;
    private final int port;
    private final ControlPanel controlPanel;
    private Connection connection;
    private volatile boolean running;

    public SensorNodeClient(String sensorNodeId, String host, int port, ControlPanel controlPanel) {
        this.sensorNodeId = sensorNodeId;
        this.host = host;
        this.port = port;
        this.controlPanel = controlPanel;
        this.running = false;
    }

    /**
     * Starts the client on its own thread.
     */
    public void start() {
        Thread thread = new Thread(this, "SensorClient-" + sensorNodeId);
        thread.start();
    }

    /**
     * Main run loop.
     * - Connects to the sensor node.
     * - Performs handshake.
     * - Listens for incoming messages and forwards them to ControlPanel.
     * 
     * @throws IOException if connection fails
     */
    @Override
    public void run() {
        try {
            // 1) Connect using TcpClient
            TcpClient tcpClient = new TcpClient(host, port);
            tcpClient.connect();
            this.connection = tcpClient.getConnection();
            this.running = true;

            System.out.println("[SensorNodeClient] >> Connected to control panel at " + host + ":" + port);

            // 2) Handshake
            Message hello = new Message(MessageType.HELLO, sensorNodeId, "");
            sendMessage(hello);

            // 3) Loop
            listenLoop();
        } catch (IOException e) {
            System.err.println("[SensorNodeClient] >> Connection error on " + sensorNodeId + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Listens for incoming messages from the sensor node.
     * Runs in its own thread and forwards messages to ControlPanel.
     * 
     * @throws IOException if connection fails
     */
    private void listenLoop() throws IOException {
        while (running && connection.isOpen()) {
            String line = connection.recvUtf();
            if (line == null || line.isBlank()) {
                continue;
            }

            Message msg = Message.fromProtocolString(line);
            if (msg == null) {
                System.err.println("[SensorNodeClient] >> Received invalid message from " + sensorNodeId + ": " + line);
                continue;
            }

            // Forward to ControlPanel
            controlPanel.handleIncomingMessage(sensorNodeId, msg);
        }
    }

    /**
     * Sends a message to the sensor node.
     * 
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        if (connection == null || !connection.isOpen()) {
            System.err.println("[SensorNodeClient] >> Cannot send, connection closed for node " + sensorNodeId);
            return;
        }

        try {
            connection.sendUtf(message.toProtocolString());
        } catch (IOException e) {
            System.err.println(
                    "[SensorNodeClient] >> Failed to send message from " + sensorNodeId + ": " + e.getMessage());
            running = false;
        }
    }

    /**
     * Sends a command to the sensor node.
     * 
     * @param actuatorType the type of actuator
     * @param state        the desired state
     */
    public void sendCommand(String actuatorType, boolean state) {
        String commandData = actuatorType.toLowerCase() + ":" + (state ? "1" : "0");
        Message command = new Message(MessageType.COMMAND, sensorNodeId, commandData);
        sendMessage(command);
    }

    /**
     * Stops the client and closes the connection.
     */
    public void stop() {
        running = false;
    }

    /**
     * Cleans up resources on shutdown.
     */
    private void cleanup() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException ignored) {
        }
        System.out.println("[SensorNodeClient] >> Closed connection for node " + sensorNodeId);

    }
}
