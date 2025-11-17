package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.sensor.Sensor;

import java.util.List;

/**
 * Actuator representing a window opener.
 * <p>
 * When ON, the window is considered "open". This could be used in the GUI
 * and/or influence temperature/humidity sensors in a more advanced way.
 */
public class WindowOpenerActuator extends Actuator {

    public WindowOpenerActuator(String deviceId) {
        super(deviceId, ActuatorType.WINDOW_OPENER);
    }

    @Override
    public void applyEffect(List<Sensor> sensors) {
        // TODO:
        // if ON, move temperature and humidity gradually towards outside conditions.
        // In a simulation define outside conditions somewhere.
    }
}