package group6.entity.device;

/**
 * Defines all available sensor types that a SensorNode can host.
 * Each sensor type includes a default measurement unit.
 * 
 * @author dotDennis
 * @since 0.1.0
 */
public enum SensorType {
    TEMPERATURE("°C"),
    HUMIDITY("%"),
    LIGHT("lux"),
    PH("pH"),
    WIND_SPEED("m/s"),
    FERTILIZER("mg/L");

    private final String defaultUnit;

    SensorType(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }
}