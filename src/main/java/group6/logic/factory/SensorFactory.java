package group6.logic.factory;

import group6.entity.device.SensorType;
import group6.entity.device.sensor.*;

/**
 * Factory helper for instantiating sensors by {@link SensorType}.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public final class SensorFactory {

    private SensorFactory() {
    }

    /**
     * Creates a sensor instance for the given type and device id.
     *
     * @param type     sensor type
     * @param deviceId desired device id
     * @return sensor implementation
     */
    public static Sensor createSensor(SensorType type, String deviceId) {
        if (type == null) {
            throw new IllegalArgumentException("Sensor type cannot be null");
        }
        String id = validateDeviceId(deviceId);

        return switch (type) {
            case TEMPERATURE -> new TemperatureSensor(id);
            case HUMIDITY -> new HumiditySensor(id);
            case LIGHT -> new LightSensor(id);
            case PH -> new PhSensor(id);
            case WIND_SPEED -> new WindSensor(id);
            case FERTILIZER -> new FertilizerSensor(id);
        };
    }

    private static String validateDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("Device id cannot be blank");
        }
        return deviceId.trim();
    }
}
