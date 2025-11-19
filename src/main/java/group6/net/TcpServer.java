package group6.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import group6.entity.node.ClientHandler;
import group6.entity.node.SensorNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP Server to handle incoming connections.
 * Wrapping them in Connection objects.
 *
 * @author dotDennis
 * @since 0.1.0
 */
public class TcpServer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);
    private final int port;
    private final SensorNode sensorNode;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private final List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

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

        LOGGER.info("Listening on port {}", port);

        while (running) {
            try {
                Socket socket = serverSocket.accept(); // Blocking call
                LOGGER.info("Control panel connected: {}", socket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(socket, sensorNode);
                clientHandlers.add(handler);
                Thread handlerThread = new Thread(() -> {
                    try {
                        handler.run();
                    } finally {
                        clientHandlers.remove(handler);
                    }
                }, "client-" + socket.getPort());
                handlerThread.start();
            } catch (SocketException e) {
                if (running) {
                    throw e;
                }
                LOGGER.debug("Socket closed for server on port {}", port);
                break;
            }
        }
    }

    /**
     * Stops the servers and closes the listen socket.
     */
    public void stop() {
        running = false;
        LOGGER.info("Stopping server on port {}", port);
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
                LOGGER.debug("Server socket already closed.", ignored);
            }
        }
        synchronized (clientHandlers) {
            for (ClientHandler handler : new ArrayList<>(clientHandlers)) {
                handler.stop();
            }
            clientHandlers.clear();
        }
    }

    /**
     * 
     */
}
