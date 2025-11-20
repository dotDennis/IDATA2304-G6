package group6.net;

import group6.protocol.Message;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP Client wrapper for connecting to a remote node (TCP Server).
 * Uses {@link Connection} for framed I/O and {@link Message} for protoco
 * payloads.
 * 
 * <p>Example usage:
 * 
 * <pre>
 * TcpClient client = new TcpClient("localhost", 12345);
 * client.connect();
 * 
 * Message hello = new Message(MessageType.HELLO, 'control-01', "Hello world");
 * client.sendMessage(hello);
 * 
 * Message response = client.receiveMessage();
 * </pre>
 * 
 * @author dotDennis
 * @since 0.1.0
 */

public class TcpClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(TcpClient.class);
  private final String host;
  private final int port;
  private Connection connection;

  /**
   * Creates a client for a given host/port.
   * 
   * @param host the remote host (IP or hostname)
   * @param port the remote TCP port
   */
  public TcpClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Opens a TCP connection and wraps it in a {@link Connection}.
   * 
   * @throws IOException if the connection fails
   */
  public void connect() throws IOException {
    Socket socket = new Socket(host, port);
    this.connection = new Connection(socket);
    LOGGER.info("Connected to {}", connection.getRemoteAddress());
  }

  /**
   * Checks if the client is connection is open.
   * 
   * @return true if connected, false otherwise
   */
  public boolean isConnected() {
    return connection != null && connection.isOpen();
  }

  /**
   * Closes the client connection.
   */
  public void close() {
    if (connection != null) {
      try {
        connection.close();
      } catch (IOException ignored) {
        LOGGER.debug("Connection already closed.", ignored);
      }
    }
  }

  /**
   * Sends a protocol {@link Message} to the server.
   * 
   * @param message the message to send
   * @throws IllegalStateException if not connected
   * @throws IOException           on I/O errors
   */
  public void sendMessage(Message message) throws IOException {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected to server");
    }
    connection.sendUtf(message.toProtocolString());
  }

  /**
   * Blocks to receive a protocol {@link Message} from the server.
   * 
   * @return the received message, or null if parsing failed
   * @throws IllegalStateException if not connected
   * @throws IOException           on I/O errors
   */
  public Message receiveMessage() throws IOException {
    if (!isConnected()) {
      throw new IllegalStateException("Client not connected to the server");
    }
    String protocolString = connection.recvUtf();
    if (protocolString == null || protocolString.isBlank()) {
      return null;
    }
    return Message.fromProtocolString(protocolString);
  }

  /**
   * Exposes the underlying connection.
   * To be used for advanced usage like setting timeouts.
   */
  public Connection getConnection() {
    return connection;
  }
}
