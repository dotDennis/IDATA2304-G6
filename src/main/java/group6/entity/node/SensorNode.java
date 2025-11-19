package group6.entity.node;

import java.util.ArrayList;
import java.util.List;

import group6.entity.device.actuator.Actuator;
import group6.entity.device.sensor.Sensor;

/**
 * SensorNode represents a node that contains sensors/actuators in the system.
 * It extends the abstract Node class.
 * 
 * @author dotDennis, Fidjor
 * @since 0.1.0
 */
public class SensorNode extends Node {

    private final List<Actuator> actuators;
    private final List<Sensor> sensors;
    private long interval = 5000;

    /**
     * Constructs a SensorNode with the specified ID.
     * 
     * @param nodeId
     */
    public SensorNode(String nodeId) {
        super(nodeId, NodeType.SENSOR);
        this.actuators = new ArrayList<>();
        this.sensors = new ArrayList<>();
    }

    // Kanskje lage en helper method for desse under, så vi kan bruke en felles
    // removeDevice og addDevice f.eks?

    // ---------- Methods ----------
    /**
     * Adds an actuator to the SensorNode.
     * 
     * @param actuator the actuator to add
     */
    public void addActuator(Actuator actuator) {
        if (actuator == null) {
            throw new IllegalArgumentException("actuator cannot be null");
        }
        this.actuators.add(actuator);
    }

    /**
     * Adds a sensor to the SensorNode.
     * 
     * @param sensor the sensor to add
     */
    public void addSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("sensor cannot be null");
        }
        this.sensors.add(sensor);
    }

    /**
     * Removes a sensor from the SensorNode.
     * 
     * @param sensor the sensor to remove
     * @throws IllegalArgumentException if sensor is null
     * @return true if the sensor was removed, false otherwise
     */
    public boolean removeSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("sensor cannot be null");
        }
        return this.sensors.remove(sensor);
    }

    /**
     * Removes an actuator from the SensorNode.
     * 
     * @param actuator the actuator to remove
     * @throws IllegalArgumentException if actuator is null
     * @return true if the actuator was removed, false otherwise
     */
    public boolean removeActuator(Actuator actuator) {
        if (actuator == null) {
            throw new IllegalArgumentException("actuator cannot be null");
        }
        return this.actuators.remove(actuator);
    }


    /**
     * Gets all current sensor readings as a formatted string for protocol.
     *Format: "sensorType:value,sensorType:value,..."
     *
     * @return the formatted sensor String
     */
    public String getSensorDataString() {
        applyActuatorEffects();

        StringBuilder data = new StringBuilder();
        for (int i = 0; i < sensors.size(); i++) {
            Sensor sensor = sensors.get(i);
            data.append(sensor.getDeviceType().toString().toLowerCase());
            data.append(":");
            data.append(sensor.readValue());
            if (i < sensors.size() - 1) {
                data.append(",");
            }
        }
        return data.toString();
    }

    /**
     * Gets all actuator states as a formatted string for protocol.
     * Format: "actuatorType: state,actuatorType: state,..."
     *
     * @return the formatted actuator string
     */
    public String getActuatorStatusString() {
        StringBuilder status = new StringBuilder();
        for (int i = 0; i < actuators.size(); i++) {
            Actuator actuator = actuators.get(i);
            status.append(actuator.getDeviceType().toString().toLowerCase());
            status.append(":");
            status.append(actuator.getState() ? "1" : "0");
            if (i < actuators.size() - 1) {
                status.append(",");
            }
        }
        return status.toString();
    }

    /**
     * Finds an actuator by its type
     *
     * @param typeName the actuator type name (heater, fan etc.)
     * @return the actuator if found, null if otherwise
     */
    public Actuator findActuatorByType(String typeName) {
        if (typeName == null) {
            return null;
        }
        for (Actuator actuator : actuators) {
            if (actuator.getDeviceType().toString().equalsIgnoreCase(typeName)) {
                return actuator;
            }
        }
        return null;
    }


    // ---------- Getters ----------
    /**
     * Returns the list of actuators associated with this SensorNode.
     * 
     * @return ArrayList of actuators
     */
    public List<Actuator> getActuators() {
        return actuators;
    }

    /**
     * Returns the list of sensors associated with this SensorNode.
     * 
     * @return ArrayList of sensors
     */
    public List<Sensor> getSensors() {
        return sensors;
    }

    public long getSensorNodeInterval() {
        return interval;
    }

    public void setSensorNodeInterval(long interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("sensorNodeInterval must be positive");
        }
        this.interval = interval;
    }

    /**
     * Applies effects from all active actuators to the attached sensors.
     */
    private void applyActuatorEffects() {
        if (actuators.isEmpty() || sensors.isEmpty()) {
            return;
        }

        for (Actuator actuator : actuators) {
            if (actuator.getState()) {
                actuator.applyEffect(sensors);
            }
        }
    }
}
