package group6.protocol;

/**
 * Protocol message types.
 * 
 * Sensors send DATA periodically.
 * Controls send COMMAND.
 * Sensors respond with SUCCESS or FAILURE.
 * KEEPALIVE messages ensure connection is alive.
 * ERROR indicates a protocol error.
 * HELLO and WELCOME are used during connection.
 * 
 * @author Fidjor, dotDennis
 * @since 0.1.0
 */
public enum MessageType {
  HELLO, // sent when a node connects
  WELCOME, // reponse to HELLO
  DATA, // sensor data or actuator status updates
  COMMAND, // control -> sensor actuator instruction
  SUCCESS, // command executed successfully
  FAILURE, // command failed
  KEEPALIVE, // periodic pings during idle
  ERROR // malformed message or protocol error
}