package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.sensor.Sensor;
import java.util.List;

/**
 * Actuator representing a door lock.
 * 
 * <p>This actuator is mostly logical (open/closed) and typically
 * does not affect environmental sensors directly.
 */
public class DoorLockActuator extends Actuator {

  /**
   * Constructs a DoorLockActuator with the specified device ID.
   * 
   * @param deviceId unique identifier for the door lock actuator
   */
  public DoorLockActuator(String deviceId) {
    super(deviceId, ActuatorType.DOOR_LOCK);
  }

  @Override
  public void applyEffect(List<Sensor> sensors) {
    // No physical effect on temperature/humidity/etc.
  }
}