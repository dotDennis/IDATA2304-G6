package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.sensor.Sensor;
import java.util.List;

/**
 * Actuator representing a generic valve (e.g. water or fertilizer).
 * 
 * <p>When ON, it could increase/decrease ph or 
 * increase fertilizer sensor values over time.
 */
public class ValveActuator extends Actuator {

  /**
   * Constructs a ValveActuator with the specified device ID.
   * 
   * @param deviceId unique identifier for the valve actuator
   */
  public ValveActuator(String deviceId) {
    super(deviceId, ActuatorType.VALVE);
  }

  @Override
  public void applyEffect(List<Sensor> sensors) {
    for (Sensor sensor : sensors) {
      SensorType type = sensor.getDeviceType();
      switch (type) {
        case HUMIDITY -> sensor.addExternalInfluence(0.6);
        case FERTILIZER -> sensor.addExternalInfluence(3.0);
        case PH -> sensor.addExternalInfluence(-0.02);
        default -> {
        }
      }
    }
  }
}
