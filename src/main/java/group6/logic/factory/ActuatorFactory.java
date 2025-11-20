package group6.logic.factory;

import group6.entity.device.ActuatorType;
import group6.entity.device.actuator.Actuator;
import group6.entity.device.actuator.DoorLockActuator;
import group6.entity.device.actuator.FanActuator;
import group6.entity.device.actuator.HeaterActuator;
import group6.entity.device.actuator.LightSwitchActuator;
import group6.entity.device.actuator.ValveActuator;
import group6.entity.device.actuator.WindowOpenerActuator;

/**
 * Factory helper for instantiating actuators by {@link ActuatorType}.
 */
public final class ActuatorFactory {

  private ActuatorFactory() {
  }

  /**
   * Creates an actuator instance for the given type and device id.
   *
   * @param type     actuator type
   * @param deviceId desired device id
   * @return actuator implementation
   */
  public static Actuator createActuator(ActuatorType type, String deviceId) {
    if (type == null) {
      throw new IllegalArgumentException("Actuator type cannot be null");
    }
    String id = validateDeviceId(deviceId);

    return switch (type) {
      case FAN -> new FanActuator(id);
      case HEATER -> new HeaterActuator(id);
      case WINDOW_OPENER -> new WindowOpenerActuator(id);
      case VALVE -> new ValveActuator(id);
      case DOOR_LOCK -> new DoorLockActuator(id);
      case LIGHT_SWITCH -> new LightSwitchActuator(id);
    };
  }

  private static String validateDeviceId(String deviceId) {
    if (deviceId == null || deviceId.isBlank()) {
      throw new IllegalArgumentException("Device id cannot be blank");
    }
    return deviceId.trim();
  }
}
