package group6.entity.device.sensor;

import group6.entity.device.SensorType;

/**
 * Temperature sensor implementation.
 *
 * @author dotDennis
 * @since 0.2.0
 */
public class TemperatureSensor extends Sensor {

    /**
     * Creates a temperature sensor.
     *
     * @param deviceId e.g. "temp-01"
     */
    public TemperatureSensor(String deviceId) {
        super(
                deviceId,
                SensorType.TEMPERATURE,
                10.0, // realistic greenhouse min °C
                35.0 // realistic greenhouse max °C
        );
    }

    /**
     * Simulates a temperature reading.
     * Uses random walk with a small step to avoid jitter.
     */
    @Override
    public double readValue() {
        // Step size ~0.3°C per update gives very natural movement
        return randomWalk(0.3);
    }
}