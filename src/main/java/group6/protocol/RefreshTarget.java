package group6.protocol;

/**
 * RefreshTarget enum represents the target of a refresh command
 * sent from the control panel to a sensor node.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public enum RefreshTarget {
  SENSORS("sensors"),
  ACTUATORS("actuators"),
  ALL("all");

  private final String commandValue;

  RefreshTarget(String commandValue) {
    this.commandValue = commandValue;
  }

  public String getCommandValue() {
    return commandValue;
  }
}
