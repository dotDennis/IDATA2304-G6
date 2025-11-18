package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.sensor.Sensor;

import java.util.List;

/**
 * Actuator representing a generic valve (e.g. water or fertilizer).
 * <p>
 * When ON, it could increase/decrease ph or increase fertilizer sensor values over time.
 */
public class ValveActuator extends Actuator {

    public ValveActuator(String deviceId) {
        super(deviceId, ActuatorType.VALVE);
    }

    @Override
    public void applyEffect(List<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            SensorType type = sensor.getDeviceType();
            switch (type) {
                case HUMIDITY -> sensor.manualAdjust(0.6);
                case FERTILIZER -> sensor.manualAdjust(3.0);
                case PH -> sensor.manualAdjust(-0.02);
                default -> {
                }
            }
        }
    }
}
