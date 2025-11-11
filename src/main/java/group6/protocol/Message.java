package group6.protocol;

/**
 * Represents a protocol message that can be sent between nodes.
 * Handles parsing and building of protocol messages.
 *
 * @author Fidjor
 * @since 0.1.0
 */

public class Message {

  private final String messageType;
  private final String nodeId;
  private final String data;


  /**
   * Creates a message with type, nodeId and data.
   *
   * @param messageType the type of the message (SENSOR_DATA, COMMAND, etc.)
   * @param nodeId the nodeID
   * @param data the message data
   */
  public Message(String messageType, String nodeId, String data) {
    this.messageType = messageType;
    this.nodeId = nodeId;
    this.data = data;
  }

  /**
   * Converts this message to protocol format string.
   * Format: TYPE|nodeId|data
   *
   * @return the formatted message string
   */

  public String toProtocolString() {
    if (data == null || data.isEmpty()) {
      return messageType + "|" + nodeId;
    }
    return messageType + "|" + nodeId + "|" + data;
  }

  /**
   * Parses a protocol string into a Message object
   *
   * @param protocolString the string to parse (SENSOR_DATA|1|temp:18.5)
   * @return Message object, or null if parsing fails
   */
  public static Message fromProtocolString(String protocolString) {
    if (protocolString == null || protocolString.isEmpty()) {
      return null;
    }
    String[] parts = protocolString.split("\\|");
    if (parts.length < 2) {
      return null;
    }
    String type = parts[0];
    String nodeId = parts[1];
    String data = parts.length > 2 ? parts[2] : "";

    return new Message(type, nodeId, data);
  }

  //Getters
  public String getMessageType() {
    return messageType;
  }
  public String getNodeId() {
    return nodeId;
  }
  public String getData() {
    return data;
  }
}
