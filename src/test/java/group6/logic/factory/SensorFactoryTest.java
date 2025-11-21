package group6.logic.factory;

import group6.entity.device.SensorType;
import group6.entity.device.sensor.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SensorFactory.
 *
 * Tests verify:
 * Sensor creation for all types (Temperature, Humidity, Light, pH, Wind, Fertilizer)
 * Device ID validation (trimming, null/empty/blank checks)
 * Sensor type validation
 * Properties of created sensors (valid ranges, functionality)
 */
class SensorFactoryTest {

  /**
   * Tests for creating sensors of all supported types.
   */
  @Nested
  @DisplayName("Sensor Creation - All Types")
  class SensorCreationTests {

    /**
     * Verifies TemperatureSensor can be created with correct properties.
     */
    @Test
    @DisplayName("Create TemperatureSensor")
    void testCreateTemperatureSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.TEMPERATURE, "temp-01");

      assertNotNull(sensor);
      assertInstanceOf(TemperatureSensor.class, sensor);
      assertEquals("temp-01", sensor.getDeviceId());
      assertEquals(SensorType.TEMPERATURE, sensor.getDeviceType());
    }

    /**
     * Verifies HumiditySensor can be created with correct properties.
     */
    @Test
    @DisplayName("Create HumiditySensor")
    void testCreateHumiditySensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.HUMIDITY, "humid-01");

      assertNotNull(sensor);
      assertInstanceOf(HumiditySensor.class, sensor);
      assertEquals("humid-01", sensor.getDeviceId());
      assertEquals(SensorType.HUMIDITY, sensor.getDeviceType());
    }

    /**
     * Verifies LightSensor can be created with correct properties.
     */
    @Test
    @DisplayName("Create LightSensor")
    void testCreateLightSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.LIGHT, "light-01");

      assertNotNull(sensor);
      assertInstanceOf(LightSensor.class, sensor);
      assertEquals("light-01", sensor.getDeviceId());
      assertEquals(SensorType.LIGHT, sensor.getDeviceType());
    }

    /**
     * Verifies PhSensor can be created with correct properties.
     */
    @Test
    @DisplayName("Create PhSensor")
    void testCreatePhSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.PH, "ph-01");

      assertNotNull(sensor);
      assertInstanceOf(PhSensor.class, sensor);
      assertEquals("ph-01", sensor.getDeviceId());
      assertEquals(SensorType.PH, sensor.getDeviceType());
    }

    /**
     * Verifies WindSensor can be created with correct properties.
     */
    @Test
    @DisplayName("Create WindSensor")
    void testCreateWindSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.WIND_SPEED, "wind-01");

      assertNotNull(sensor);
      assertInstanceOf(WindSensor.class, sensor);
      assertEquals("wind-01", sensor.getDeviceId());
      assertEquals(SensorType.WIND_SPEED, sensor.getDeviceType());
    }

    /**
     * Verifies FertilizerSensor can be created with correct properties.
     */
    @Test
    @DisplayName("Create FertilizerSensor")
    void testCreateFertilizerSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.FERTILIZER, "fertilizer-01");

      assertNotNull(sensor);
      assertInstanceOf(FertilizerSensor.class, sensor);
      assertEquals("fertilizer-01", sensor.getDeviceId());
      assertEquals(SensorType.FERTILIZER, sensor.getDeviceType());
    }

    /**
     * Verifies all SensorType enum values can be successfully created.
     * Iterates through all enum values to ensure factory completeness.
     */
    @Test
    @DisplayName("All SensorTypes can be created")
    void testAllSensorTypesCanBeCreated() {
      for (SensorType type : SensorType.values()) {
        Sensor sensor = SensorFactory.createSensor(type, "test-id");

        assertNotNull(sensor, "Failed to create sensor for type: " + type);
        assertEquals(type, sensor.getDeviceType());
      }
    }
  }

  /**
   * Tests for device ID validation and sanitization.
   */
  @Nested
  @DisplayName("Device ID Validation")
  class DeviceIdValidationTests {

    /**
     * Verifies createSensor() trims leading and trailing whitespace from device IDs.
     */
    @Test
    @DisplayName("createSensor() trims whitespace from device ID")
    void testCreateSensorTrimsDeviceId() {
      Sensor sensor = SensorFactory.createSensor(SensorType.TEMPERATURE, "  temp-01  ");

      assertEquals("temp-01", sensor.getDeviceId());
    }

    /**
     * Verifies createSensor() throws IllegalArgumentException for null device ID.
     */
    @Test
    @DisplayName("createSensor() throws exception for null device ID")
    void testCreateSensorNullDeviceId() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(SensorType.TEMPERATURE, null);
      });

      assertTrue(exception.getMessage().contains("blank") ||
              exception.getMessage().contains("Device id"));
    }

    /**
     * Verifies createSensor() throws IllegalArgumentException for empty device ID.
     */
    @Test
    @DisplayName("createSensor() throws exception for empty device ID")
    void testCreateSensorEmptyDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(SensorType.TEMPERATURE, "");
      });
    }

    /**
     * Verifies createSensor() throws IllegalArgumentException for blank device ID.
     * Blank is defined as a string containing only whitespace characters.
     */
    @Test
    @DisplayName("createSensor() throws exception for blank device ID")
    void testCreateSensorBlankDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(SensorType.TEMPERATURE, "   ");
      });
    }
  }

  /**
   * Tests for sensor type validation.
   */
  @Nested
  @DisplayName("Sensor Type Validation")
  class SensorTypeValidationTests {

    /**
     * Verifies createSensor() throws IllegalArgumentException for null sensor type.
     */
    @Test
    @DisplayName("createSensor() throws exception for null sensor type")
    void testCreateSensorNullType() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(null, "temp-01");
      });

      assertTrue(exception.getMessage().contains("null") ||
              exception.getMessage().contains("type"));
    }
  }

  /**
   * Tests for properties and behavior of created sensors.
   */
  @Nested
  @DisplayName("Created Sensor Properties")
  class SensorPropertiesTests {

    /**
     * Verifies all created sensors have valid min/max ranges.
     * Ensures minValue is less than maxValue for proper bounds checking.
     */
    @Test
    @DisplayName("Created sensors have valid min/max ranges")
    void testCreatedSensorsHaveValidRanges() {
      for (SensorType type : SensorType.values()) {
        Sensor sensor = SensorFactory.createSensor(type, "test-01");

        assertTrue(sensor.getMinValue() < sensor.getMaxValue(),
                type + " should have min < max");
      }
    }

    /**
     * Verifies created sensors are fully functional.
     * Tests that sensors can:
     * Read values within their valid range
     * Apply manual adjustments successfully
     */
    @Test
    @DisplayName("Created sensors are fully functional")
    void testCreatedSensorsAreFunctional() {
      Sensor sensor = SensorFactory.createSensor(SensorType.TEMPERATURE, "temp-01");

      double value = sensor.readValue();
      assertTrue(value >= sensor.getMinValue());
      assertTrue(value <= sensor.getMaxValue());

      double adjusted = sensor.manualAdjust(1.0);
      assertNotEquals(Double.NaN, adjusted);
    }

    /**
     * Verifies multiple sensors of the same type can be created with different IDs.
     * Ensures factory creates new instances rather than returning singletons.
     */

    @Test
    @DisplayName("Multiple sensors can be created with same type")
    void testMultipleSensorsWithSameType() {
      Sensor sensor1 = SensorFactory.createSensor(SensorType.TEMPERATURE, "temp-01");
      Sensor sensor2 = SensorFactory.createSensor(SensorType.TEMPERATURE, "temp-02");

      assertNotSame(sensor1, sensor2);
      assertEquals("temp-01", sensor1.getDeviceId());
      assertEquals("temp-02", sensor2.getDeviceId());
    }
  }
}