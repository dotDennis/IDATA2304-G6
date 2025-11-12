package group6.protocol;

/**
 * Protocol message types.
 * @author Fidjor, dotDennis
 * @since 0.1.0
 */
public enum MessageType {
    HELLO,
    WELCOME,
    DATA,
    COMMAND,
    SUCCESS,
    FAILURE,
    KEEPALIVE,
    ERROR
}