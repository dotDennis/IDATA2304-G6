package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
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
        // TODO:
        // if ON, increase/decrase ph or increase fertilizer concentration sensors.
    }
}