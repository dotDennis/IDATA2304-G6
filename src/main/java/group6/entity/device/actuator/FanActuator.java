package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.sensor.Sensor;

import java.util.List;

/**
 * Actuator representing a fan in the greenhouse.
 * <p>
 * When ON, this actuator will to reduce temperature and/or humidity
 * over time. Simulation effect will be added in {@link #applyEffect(List)}.
 */
public class FanActuator extends Actuator {

    public FanActuator(String deviceId) {
        super(deviceId, ActuatorType.FAN);
    }

    @Override
    public void applyEffect(List<Sensor> sensors) {
        // TODO:
        // - decrease temperature a bit
        // - decrease humidity a bit
    }
}