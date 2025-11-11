package group6.protocol;

/**
 * Constants for protocol message
 *
 * @author Fidjor
 * @since 0.1.0
 */

public class MessageType {

  public static final String SENSOR_DATA = "SENSOR_DATA";
  public static final String ACTUATOR_STATUS = "ACTUATOR_STATUS";
  public static final String COMMAND = "COMMAND";
  public static final String ACK = "ACK";
  public static final String ERROR = "ERROR";

  private MessageType() {
  }

}
