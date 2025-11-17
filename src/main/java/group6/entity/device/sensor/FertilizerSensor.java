package group6.entity.device.sensor;

import group6.entity.device.SensorType;

/**
 * Fertilizer sensor implementation.
 *
 * @author dotDennis
 * @since 0.2.0
 */
public class FertilizerSensor extends Sensor {

    /**
     * Creates a fertilizer sensor.
     *
     * @param deviceId e.g. "fertilizer-01"
     */
    public FertilizerSensor(String deviceId) {
        super(deviceId, SensorType.FERTILIZER, 0.0, 500.0);
    }

    /**
     * Simulates a fertilizer reading.
     * Uses random walk with a medium-large step to avoid jitter.
     */
    @Override
    public double readValue() {
        return randomWalk(5.0);
    }
}