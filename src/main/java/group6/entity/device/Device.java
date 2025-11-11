package group6.entity.device;

public abstract class Device<Type extends Enum<Type>> {
    private final String deviceId;
    private final Type deviceType;

    /**
     * Constructs a Device with the specified ID and type.
     * 
     * @param deviceId   unique identifier for the device
     * @param deviceType type of the device
     * @throws IllegalArgumentException if deviceId is null/blank or deviceType is
     *                                  null
     */
    protected Device(String deviceId, Type deviceType) {
        this.deviceId = validateDeviceId(deviceId);
        this.deviceType = validateDeviceType(deviceType);
    }

    // ---------- Helpers ----------
    protected static String validateDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException("deviceId cannot be null or blank");
        }
        return deviceId.trim();
    }

    protected static <Type extends Enum<Type>> Type validateDeviceType(Type deviceType) {
        if (deviceType == null) {
            throw new IllegalArgumentException("deviceType cannot be null");
        }
        return deviceType;
    }

    // ---------- Getters ----------
    public String getDeviceId() {
        return deviceId;
    }

    public Type getDeviceType() {
        return deviceType;
    }
}
