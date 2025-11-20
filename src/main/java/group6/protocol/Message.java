package group6.protocol;

import java.util.Objects;

/**
 * Represents a protocol message that can be sent between nodes.
 * Handles parsing and building of protocol messages.
 */

public class Message {

  private final MessageType messageType;
  private final String nodeId;
  private final String data;

  /**
   * Creates a message with type, nodeId and data.
   *
   * @param messageType the type of the message (DATA, COMMAND, etc.)
   * @param nodeId      the nodeID
   * @param data        the message data
   */
  public Message(MessageType messageType, String nodeId, String data) {
    this.messageType = Objects.requireNonNull(messageType, "messageType cannot be null");
    if (nodeId == null || nodeId.isBlank()) {
      throw new IllegalArgumentException("nodeId cannot be null or blank");
    }
    this.nodeId = nodeId;
    this.data = (data == null) ? "" : data;
  }

  /**
   * Converts this message to protocol format string.
   * Format: TYPE|nodeId|data
   *
   * @return the formatted message string
   */

  public String toProtocolString() {
    if (data == null || data.isEmpty()) {
      return messageType.name() + "|" + nodeId;
    }
    return messageType.name() + "|" + nodeId + "|" + data;
  }

  /**
   * Parses a protocol string into a Message object.
   * 
   * <p>Expected format: TYPE|nodeId|data (data is optional, but not null)
   * example: SENSOR|sensor-01|temperature:18.5,humidity:60.0
   *
   * @param protocolString the string to parse
   * @return Message object, or null if parsing fails
   */
  public static Message fromProtocolString(String protocolString) {
    if (protocolString == null || protocolString.isBlank()) {
      return null;
    }
    String[] parts = protocolString.split("\\|", 3);
    if (parts.length < 2) {
      return null;
    }

    MessageType type;
    try {
      type = MessageType.valueOf(parts[0].trim());
    } catch (IllegalArgumentException e) {
      return null;
    }

    String nodeId = parts[1].trim();
    if (nodeId.isEmpty()) {
      return null;
    }

    String data = (parts.length == 3) ? parts[2] : "";

    return new Message(type, nodeId, data);
  }

  // Getters
  public MessageType getMessageType() {
    return messageType;
  }

  public String getNodeId() {
    return nodeId;
  }

  public String getData() {
    return data;
  }
}
