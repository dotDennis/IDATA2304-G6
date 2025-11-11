package group6.entity.node;

import java.util.ArrayList;
import java.util.List;

import group6.entity.device.Actuator;
import group6.entity.device.Sensor;

/**
 * SensorNode represents a node that contains sensors/actuators in the system.
 * It extends the abstract Node class.
 * 
 * @author dotDennis
 * @since 0.1.0
 */
public class SensorNode extends Node {

    private final List<Actuator> actuators;
    private final List<Sensor> sensors;

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

    /**
     * Removes an actuator from the SensorNode.
     * 
     * @param actuator the actuator to remove
     */

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
}
