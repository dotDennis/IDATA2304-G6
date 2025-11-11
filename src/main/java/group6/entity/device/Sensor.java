package group6.entity.device;
import java.util.Random;

public class Sensor extends Device<SensorType>{

  private double currentValue;
  
  /**
   * Constructs a Device with the specified ID and type.
   *
   * @param deviceId   unique identifier for the device
   * @param deviceType type of the device
   * @throws IllegalArgumentException if deviceId is null/blank or deviceType is
   *                                  null
   */
  public Sensor(String deviceId, SensorType deviceType) {
    super(deviceId, deviceType);
  }

  /**
   * Simulates reading a value from the sensor.
   * Generates a value based on the sensor type.
   *
   * @return the current sensor reading.
   */
  public double readValue() {

    Random random = new Random();
    switch (getDeviceType()) {
      case TEMPERATURE:
        currentValue = 15 + (random.nextDouble() * 15);
        return currentValue;
      case HUMIDITY:
        currentValue = 30 + (random.nextDouble() * 50);
        return currentValue;
      case LIGHT:
        currentValue = 0 + (random.nextDouble() * 1000);
        return currentValue;
      case PH:
        currentValue = 5.5 + (random.nextDouble() * 3);
        return currentValue;
      case WIND_SPEED:
        currentValue = 0 + (random.nextDouble() * 20);
        return currentValue;
      case FERTILIZER:
        currentValue = 0 + (random.nextDouble() * 500);
        return currentValue;
      default:
        return 0.0;
    }
  }
}
