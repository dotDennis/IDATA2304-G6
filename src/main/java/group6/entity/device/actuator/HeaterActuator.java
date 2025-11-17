package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.sensor.Sensor;

import java.util.List;

/**
 * Actuator representing a heater in the greenhouse.
 * <p>
 * When ON, this actuator helps raise the temperature in the
 * environment over time (simulation logic will be added in {@link #applyEffect(List)}).
 */
public class HeaterActuator extends Actuator {

    public HeaterActuator(String deviceId) {
        super(deviceId, ActuatorType.HEATER);
    }

    @Override
    public void applyEffect(List<Sensor> sensors) {
        // TODO:
        // Look for TEMPERATURE sensors and
        // slightly increase their values when the heater is ON.
    }
}