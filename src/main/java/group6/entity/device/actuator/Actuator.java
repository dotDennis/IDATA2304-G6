package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.Device;

/**
 * Base class for all actuators.
 *
 * All actuators start in OFF state.
 * Concrete subclasses only define the actuator type, no custom state setup.
 *
 * @author dotDennis
 * @since 0.1.0
 */
public abstract class Actuator extends Device<ActuatorType> {

    private boolean state = false; // ALWAYS OFF on creation

    /**
     * Constructs an actuator with the specified ID and type.
     * Actuator always starts OFF.
     *
     * @param deviceId   unique identifier for the actuator
     * @param deviceType type of the actuator
     */
    protected Actuator(String deviceId, ActuatorType deviceType) {
        super(deviceId, deviceType);
    }

    // ---------- State ----------
    /**
     * Turns the actuator ON or OFF.
     *
     * @param on true to turn ON, false to turn OFF
     */
    public void setState(boolean on) {
        this.state = on;
    }

    /**
     * Gets the current state of the actuator.
     * 
     * @return true if ON, false if OFF
     */
    public boolean getState() {
        return state;
    }

}