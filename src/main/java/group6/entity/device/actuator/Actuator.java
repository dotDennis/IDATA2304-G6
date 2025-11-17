package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.Device;

import java.time.LocalDateTime;

/**
 * Base class for all actuators.
 * Holds common metadata + ON/OFF state and last-changed timestamp.
 *
 * @author dotDennis
 * @since 0.1.0
 */
public abstract class Actuator extends Device<ActuatorType> {

    private boolean state;
    private LocalDateTime lastChanged;

    /**
     * Base constructor for all actuators.
     *
     * @param deviceId unique ID for this actuator (e.g. "heater-01")
     * @param type     actuator type enum
     */
    protected Actuator(String deviceId, ActuatorType type) {
        super(deviceId, type);
        this.state = false;
        this.lastChanged = LocalDateTime.now();
    }

    // ---------- Core state API ----------

    /**
     * Turns the actuator ON.
     */
    public final void turnOn() {
        setState(true);
    }

    /**
     * Turns the actuator OFF.
     */
    public final void turnOff() {
        setState(false);
    }

    /**
     * Toggles the actuator state.
     */
    public final void toggle() {
        setState(!state);
    }

    /**
     * Sets the actuator state (true = ON, false = OFF).
     * Subclasses can hook into onStateChanged().
     *
     * @param newState desired state
     */
    public void setState(boolean newState) {
        if (this.state == newState) {
            return; // no-op
        }
        this.state = newState;
        this.lastChanged = LocalDateTime.now();
        onStateChanged(newState);
    }

    /**
     * Hook for subclasses when state changes (e.g. logging, internal adjustment).
     * Default implementation does nothing.
     *
     * @param newState new state (true = ON, false = OFF)
     */
    protected void onStateChanged(boolean newState) {
        // subclass hook – optional
    }

    // ---------- Getters ----------

    /**
     * @return true if actuator is ON
     */
    public boolean isOn() {
        return state;
    }

    /**
     * @return raw state flag
     */
    public boolean getState() {
        return state;
    }

    public LocalDateTime getLastChanged() {
        return lastChanged;
    }

    /**
     * Convenience: human-readable name from ActuatorType.
     */
    public String getDisplayName() {
        return getDeviceType().getDisplayName();
    }
}