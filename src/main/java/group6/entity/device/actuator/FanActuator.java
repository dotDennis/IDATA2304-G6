package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.sensor.Sensor;
import java.util.List;

/**
 * Actuator representing a fan in the greenhouse.
 * 
 * <p>When ON, this actuator will to reduce temperature and/or humidity
 * over time. Simulation effect will be added in {@link #applyEffect(List)}.
 */
public class FanActuator extends Actuator {

  /**
   * Constructs a FanActuator with the specified device ID.
   * 
   * @param deviceId unique identifier for the fan actuator
   */
  public FanActuator(String deviceId) {
    super(deviceId, ActuatorType.FAN);
  }

  @Override
  public void applyEffect(List<Sensor> sensors) {
    for (Sensor sensor : sensors) {
      SensorType type = sensor.getDeviceType();
      switch (type) {
        case TEMPERATURE -> sensor.addExternalInfluence(-0.3);
        case HUMIDITY -> sensor.addExternalInfluence(-0.5);
        case WIND_SPEED -> sensor.addExternalInfluence(0.4);
        default -> {
        }
      }
    }
  }
}
