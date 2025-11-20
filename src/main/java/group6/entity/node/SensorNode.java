package group6.entity.node;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import group6.entity.device.actuator.Actuator;
import group6.entity.device.sensor.Sensor;
import group6.entity.device.Device;
import group6.entity.device.DeviceUpdateListener;
import group6.protocol.DeviceKey;
import group6.protocol.SensorReading;

/**
 * Represents a node in the system that contains sensors and actuators.
 * <p>
 * A {@code SensorNode} is responsible for managing attached devices,
 * collecting readings, tracking update timestamps and notifying listeners
 * when sensors or actuators change.
 *
 * @author Fidjor, dotDennis
 * @since 0.1.0
 */
public class SensorNode extends Node implements DeviceUpdateListener {

    /**
     * Distinguishes between sensor and actuator devices in internal helpers.
     */
    private enum DeviceType {
        SENSOR,
        ACTUATOR
    }

    private final DeviceRegistry<Actuator> actuators;
    private final DeviceRegistry<Sensor> sensors;
    private long interval = 5000;
    private final DeviceUpdateTracker updateTracker;
    private final List<SensorNodeUpdateListener> updateListeners = new CopyOnWriteArrayList<>();

    /**
     * Creates a new {@code SensorNode} with the given ID.
     *
     * @param nodeId the unique identifier for this node
     */
    public SensorNode(String nodeId) {
        super(nodeId, NodeType.SENSOR);
        this.actuators = new DeviceRegistry<>();
        this.sensors = new DeviceRegistry<>();
        this.updateTracker = new DeviceUpdateTracker();
    }

    // ------- Device management -------

    /**
     * Adds an actuator to this node and registers this node as update listener.
     *
     * @param actuator the actuator to add
     *
     * @throws IllegalArgumentException if actuator is null
     */
    public void addActuator(Actuator actuator) {
        addDevice(actuator, DeviceType.ACTUATOR);
    }

    /**
     * Adds a sensor to this node and registers this node as update listener.
     *
     * @param sensor the sensor to add
     *
     * @throws IllegalArgumentException if sensor is null
     */
    public void addSensor(Sensor sensor) {
        addDevice(sensor, DeviceType.SENSOR);
    }

    /**
     * Removes a sensor from this node and unregisters this node as listener.
     *
     * @param sensor the sensor to remove
     *
     * @return true if the sensor was removed, false if it was not
     *
     * @throws IllegalArgumentException if sensor is null
     */
    public boolean removeSensor(Sensor sensor) {
        return removeDevice(sensor, DeviceType.SENSOR);
    }

    /**
     * Removes an actuator from this node and unregisters this node as listener.
     *
     * @param actuator the actuator to remove
     *
     * @return true if the actuator was removed, false if it was not
     *
     * @throws IllegalArgumentException if actuator is null
     */
    public boolean removeActuator(Actuator actuator) {
        return removeDevice(actuator, DeviceType.ACTUATOR);
    }

    // ------- Internal helpers -------

    /**
     * Adds a device to this node and registers as listener.
     *
     * @param device the device to add
     * @param type   the type of device
     *
     * @throws IllegalArgumentException if device is null
     */
    private void addDevice(Device<?> device, DeviceType type) {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        if (type == DeviceType.SENSOR) {
            sensors.add((Sensor) device);
        } else {
            actuators.add((Actuator) device);
        }

        device.addUpdateListener(this);
    }

    /**
     * Removes a device of the given type from this node and unregisters listener.
     * <p>
     * Also clears any cached timestamps and pending update markers for that
     * device ID once it has been removed.
     *
     * @param device the device to remove
     * @param type   the type of device (sensor or actuator)
     *
     * @return true if the device was removed, false otherwise
     *
     * @throws IllegalArgumentException if device is null
     */
    private boolean removeDevice(Device<?> device, DeviceType type) {
        if (device == null) {
            throw new IllegalArgumentException("device cannot be null");
        }

        boolean removed;
        if (type == DeviceType.SENSOR) {
            removed = sensors.remove((Sensor) device);
        } else {
            removed = actuators.remove((Actuator) device);
        }

        if (!removed) {
            return false;
        }

        device.removeUpdateListener(this);
        updateTracker.removeDevice(device.getDeviceId());

        return true;
    }

    // ------- Protocol -------

    /**
     * Returns all current sensor readings in protocol format.
     * <p>
     * The returned string has the form:
     * type#id:value,type#id:value,....
     * Actuator effects are applied before readings are collected.
     *
     * @return a formatted sensor data string, or an empty string if there are no
     *         sensors
     */
    public String getSensorSnapshot() {
        applyActuatorEffects();

        List<Sensor> sensorList = sensors.snapshot();
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < sensorList.size(); i++) {
            Sensor sensor = sensorList.get(i);
            SensorReading reading = SensorReading.of(
                    DeviceKey.of(sensor.getDeviceType().toString(), sensor.getDeviceId()),
                    sensor.getCurrentValue());
            data.append(reading.toProtocolString());
            if (i < sensorList.size() - 1) {
                data.append(",");
            }
        }
        return data.toString();
    }

    /**
     * Returns all actuator states in protocol format.
     * <p>
     * The returned string has the form:
     * type#id:1,type#id:0,... where 1 represents
     * actuator is ON and 0 for OFF.
     *
     * @return a formatted actuator status string, or an empty string if there
     *         are no actuators
     */
    public String getActuatorSnapshot() {
        List<Actuator> actuatorList = actuators.snapshot();
        StringBuilder status = new StringBuilder();
        for (int i = 0; i < actuatorList.size(); i++) {
            Actuator actuator = actuatorList.get(i);
            status.append(
                    DeviceKey.of(actuator.getDeviceType().toString(), actuator.getDeviceId()).toProtocolKey());
            status.append(":");
            status.append(actuator.getState() ? "1" : "0");
            if (i < actuatorList.size() - 1) {
                status.append(",");
            }
        }
        return status.toString();
    }

    // ------- Public Helpers -------

    /**
     * Finds an actuator by its type name.
     *
     * @param typeName the actuator type name (for example heater or fan)
     *
     * @return the matching actuator, or null if none matches
     */
    public Actuator findActuatorByType(String typeName) {
        if (typeName == null) {
            return null;
        }
        for (Actuator actuator : actuators.snapshot()) {
            if (actuator.getDeviceType().toString().equalsIgnoreCase(typeName)) {
                return actuator;
            }
        }
        return null;
    }

    /**
     * Finds an actuator by its device ID.
     *
     * @param deviceId the device ID to search for
     *
     * @return the matching actuator, or null if none matches
     */
    public Actuator findActuatorByDeviceId(String deviceId) {
        if (deviceId == null) {
            return null;
        }
        for (Actuator actuator : actuators.snapshot()) {
            if (actuator.getDeviceId().equalsIgnoreCase(deviceId)) {
                return actuator;
            }
        }
        return null;
    }

    /**
     * Returns the list of actuators associated with this node.
     * <p>
     * The returned list is the internal collection and can be modified. It is
     * the caller's responsibility to avoid concurrent modification issues.
     *
     * @return the list of actuators
     */
    public List<Actuator> getActuators() {
        return actuators.snapshot();
    }

    /**
     * Returns the list of sensors associated with this node.
     * <p>
     * The returned list is the internal collection and can be modified. It is
     * the caller's responsibility to avoid concurrent modification issues.
     *
     * @return the list of sensors
     */
    public List<Sensor> getSensors() {
        return sensors.snapshot();
    }

    /**
     * Returns the current reporting interval in milliseconds.
     *
     * @return the reporting interval in milliseconds
     */
    public long getSensorNodeInterval() {
        return interval;
    }

    /**
     * Sets the reporting interval in milliseconds.
     *
     * @param interval the new interval; must be positive
     *
     * @throws IllegalArgumentException if interval is not positive
     */
    public void setSensorNodeInterval(long interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("sensorNodeInterval must be positive");
        }
        this.interval = interval;
    }

    // ------- Simulation Effects -------

    /**
     * Applies effects from all active actuators to attached sensors.
     * <p>
     * If there are no actuators or no sensors, this method returns immediately
     * without performing any work.
     */
    private void applyActuatorEffects() {
        List<Actuator> actuatorList = actuators.snapshot();
        List<Sensor> sensorList = sensors.snapshot();
        if (actuatorList.isEmpty() || sensorList.isEmpty()) {
            return;
        }

        for (Actuator actuator : actuatorList) {
            if (actuator.getState()) {
                actuator.applyEffect(sensorList);
            }
        }
    }

    /**
     * Callback invoked when a device attached to this node has been updated.
     * <p>
     * This method records the update timestamp and notifies registered
     * {@link SensorNodeUpdateListener}s depending on whether a sensor or
     * actuator was updated.
     *
     * @param device the updated device, or null
     */
    @Override
    public void onDeviceUpdated(Device<?> device) {
        if (device == null) {
            return;
        }

        if (device instanceof Sensor) {
            updateTracker.recordSensorUpdate(device.getDeviceId());
            for (SensorNodeUpdateListener listener : updateListeners) {
                listener.onSensorsUpdated(this);
            }
        } else if (device instanceof Actuator) {
            updateTracker.recordActuatorUpdate(device.getDeviceId());
            for (SensorNodeUpdateListener listener : updateListeners) {
                listener.onActuatorsUpdated(this);
            }
        }
    }

    // ------- Update listeners & timestamps -------

    /**
     * Returns the last update timestamp for the given device ID.
     *
     * @param deviceId the device ID to look up
     *
     * @return the timestamp in milliseconds, or 0L if the device has
     *         not been recorded
     */
    public long getDeviceUpdateTimestamp(String deviceId) {
        return updateTracker.getTimestamp(deviceId);
    }

    /**
     * Registers a listener to receive sensor and actuator update events
     * from this node.
     *
     * @param listener the listener to register; ignored if null
     */
    public void addUpdateListener(SensorNodeUpdateListener listener) {
        if (listener != null) {
            updateListeners.add(listener);
        }
    }

    /**
     * Unregisters a previously registered update listener.
     *
     * @param listener the listener to remove; no effect if not registered
     */
    public void removeUpdateListener(SensorNodeUpdateListener listener) {
        updateListeners.remove(listener);
    }

    // ------- Pending sensor data -------

    /**
     * Consumes and returns a formatted string for sensors that have updated
     * since the last call.
     * <p>
     * After this method returns, the internal set of pending sensor updates
     * is cleared. The returned string has the form:
     * type#id:value,type#id:value,... If no sensors have updated,
     * an empty string is returned.
     *
     * @return a formatted string of pending sensor updates,
     *         or an empty string if there are none
     */
    public String drainPendingSensorUpdates() {
        if (!updateTracker.hasPendingSensors()) {
            return "";
        }

        StringBuilder data = new StringBuilder();
        for (Sensor sensor : sensors.snapshot()) {
            String deviceKey = normalizeId(sensor.getDeviceId());
            if (!updateTracker.consumePendingSensor(deviceKey)) {
                continue;
            }
            if (data.length() > 0) {
                data.append(",");
            }
            SensorReading reading = SensorReading.of(
                    DeviceKey.of(sensor.getDeviceType().toString(), sensor.getDeviceId()),
                    sensor.getCurrentValue());
            data.append(reading.toProtocolString());
        }
        return data.toString();
    }

    // ------- Normalization -------

    /**
     * Normalizes a device ID for internal key usage.
     * <p>
     * The ID is trimmed and converted to lower case using Locale#ROOT.
     * If id is null, an empty string is returned.
     *
     * @param id the raw device ID, may be null
    *
     * @return the normalized ID, never null
     */
    private static String normalizeId(String id) {
        if (id == null) {
            return "";
        }
        return id.trim().toLowerCase(Locale.ROOT);
    }
}
