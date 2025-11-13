package group6.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import group6.entity.node.ClientHandler;
import group6.entity.node.SensorNode;

/**
 * TCP Server to handle incoming connections.
 * Wrapping them in Connection objects.
 *
 * @author dotDennis
 * @since 0.1.0
 */
public class TcpServer {
    
    private final int port;
    private final SensorNode sensorNode;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public TcpServer(int port, SensorNode sensorNode) {
        this.port = port;
        this.sensorNode = sensorNode;
    }

    /**
     * Starts the TCP server to listen for incoming connections.
     * Typically runs in its own thread.
     * 
     * @throws IOException
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("[TcpServer] >> Listening on port " + port);

        while (running) {
            Socket socket = serverSocket.accept(); // Blocking call
            System.out.println("[TcpServer] >> Control panel connected: " + socket.getRemoteSocketAddress());
        
            ClientHandler handler = new ClientHandler(socket, sensorNode);
            new Thread(handler, "client-" + socket.getPort()).start();
        }
    }

    /**
     * Stops the servers and closes the listen socket.
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
                System.out.println("[TcpServer] >> Server socket closed.");
            }
        }
    }

    /**
     * 
     */
}
