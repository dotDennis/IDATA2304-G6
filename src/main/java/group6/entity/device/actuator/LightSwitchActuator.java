package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.sensor.Sensor;

import java.util.List;

/**
 * Actuator representing a light switch.
 * <p>
 * When ON, this actuator could increase the LIGHT sensor readings
 * to simulate lux values (lighting) in the greenhouse.
 */
public class LightSwitchActuator extends Actuator {

    public LightSwitchActuator(String deviceId) {
        super(deviceId, ActuatorType.LIGHT_SWITCH);
    }

    @Override
    public void applyEffect(List<Sensor> sensors) {
        // TODO::
        // if ON, slightly increase LIGHT sensor readings to simulate lamps.
    }
}