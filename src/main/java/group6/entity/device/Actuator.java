package group6.entity.device;

/**
 * SensorNode represents a node that contains sensors/actuators in the system.
 * It extends the abstract Node class.
 * 
 * @author dotDennis
 * @since 0.1.0
 */
public class Actuator extends Device<ActuatorType> {

    private boolean state;

    /**
     * Constructs an Actuator with the specified ID and type.
     * 
     * @param deviceId   unique identifier for the actuator
     * @param deviceType type of the actuator
     * @throws IllegalArgumentException if deviceId is null/blank or deviceType is
     *                                  null
     */
    public Actuator(String deviceId, ActuatorType deviceType) {
        super(deviceId, deviceType);
    }

    /**
     * Constructs an Actuator with the specified ID, type, and initial state.
     * 
     * @param deviceId   unique identifier for the actuator
     * @param deviceType type of the actuator
     * @param state      initial state of the actuator (true for ON, false for OFF)
     * @throws IllegalArgumentException if deviceId is null/blank or deviceType is
     *                                  null
     */
    public Actuator(String deviceId, ActuatorType deviceType, boolean state) {
        super(deviceId, deviceType);
        setState(state);
    }

    // ---------- Setters ----------
    public void setState(boolean state) {
        this.state = state;
    }

    // ---------- Getters ----------
    public boolean getState() {
        return state;
    }

}
