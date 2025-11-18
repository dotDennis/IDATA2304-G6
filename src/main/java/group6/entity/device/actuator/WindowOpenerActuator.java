package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.sensor.Sensor;

import java.util.List;

/**
 * Actuator representing a window opener.
 * <p>
 * When ON, the window is considered "open". This could be used in the GUI
 * and/or influence temperature/humidity sensors in a more advanced way.
 * For now simply adjusts temperature, humidity, and wind speed when applied.
 */
public class WindowOpenerActuator extends Actuator {

    public WindowOpenerActuator(String deviceId) {
        super(deviceId, ActuatorType.WINDOW_OPENER);
    }

    @Override
    public void applyEffect(List<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            SensorType type = sensor.getDeviceType();
            switch (type) {
                case TEMPERATURE -> sensor.manualAdjust(-0.2);
                case HUMIDITY -> sensor.manualAdjust(-0.3);
                case WIND_SPEED -> sensor.manualAdjust(0.5);
                default -> {
                }
            }
        }
    }
}
