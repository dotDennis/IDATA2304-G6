package group6.entity.device;

/**
 * Defines all available actuator types that can be controlled by a SensorNode.
 * Each actuator type includes a display name for better readability.
 * 
 * @author dotDennis
 * @since 0.1.0
 */
public enum ActuatorType {
    FAN("Fan"),
    HEATER("Heater"),
    WINDOW_OPENER("Window Opener"),
    VALVE("Valve"),
    DOOR_LOCK("Door Lock"),
    LIGHT_SWITCH("Light Switch");

    private final String displayName;

    ActuatorType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a human-readable name for the actuator type.
     *
     * @return formatted display name
     */
    public String getDisplayName() {
        return displayName;
    }
}