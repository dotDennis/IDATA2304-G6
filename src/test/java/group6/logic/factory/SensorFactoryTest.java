package group6.logic.factory;

import group6.entity.device.SensorType;
import group6.entity.device.sensor.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SensorFactory.
 */
class SensorFactoryTest {

  @Nested
  @DisplayName("Sensor Creation - All Types")
  class SensorCreationTests {

    @Test
    @DisplayName("Create TemperatureSensor")
    void testCreateTemperatureSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.TEMPERATURE, "temp-01");

      assertNotNull(sensor);
      assertInstanceOf(TemperatureSensor.class, sensor);
      assertEquals("temp-01", sensor.getDeviceId());
      assertEquals(SensorType.TEMPERATURE, sensor.getDeviceType());
    }

    @Test
    @DisplayName("Create HumiditySensor")
    void testCreateHumiditySensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.HUMIDITY, "humid-01");

      assertNotNull(sensor);
      assertInstanceOf(HumiditySensor.class, sensor);
      assertEquals("humid-01", sensor.getDeviceId());
      assertEquals(SensorType.HUMIDITY, sensor.getDeviceType());
    }

    @Test
    @DisplayName("Create LightSensor")
    void testCreateLightSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.LIGHT, "light-01");

      assertNotNull(sensor);
      assertInstanceOf(LightSensor.class, sensor);
      assertEquals("light-01", sensor.getDeviceId());
      assertEquals(SensorType.LIGHT, sensor.getDeviceType());
    }

    @Test
    @DisplayName("Create PhSensor")
    void testCreatePhSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.PH, "ph-01");

      assertNotNull(sensor);
      assertInstanceOf(PhSensor.class, sensor);
      assertEquals("ph-01", sensor.getDeviceId());
      assertEquals(SensorType.PH, sensor.getDeviceType());
    }

    @Test
    @DisplayName("Create WindSensor")
    void testCreateWindSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.WIND_SPEED, "wind-01");

      assertNotNull(sensor);
      assertInstanceOf(WindSensor.class, sensor);
      assertEquals("wind-01", sensor.getDeviceId());
      assertEquals(SensorType.WIND_SPEED, sensor.getDeviceType());
    }

    @Test
    @DisplayName("Create FertilizerSensor")
    void testCreateFertilizerSensor() {
      Sensor sensor = SensorFactory.createSensor(SensorType.FERTILIZER, "fertilizer-01");

      assertNotNull(sensor);
      assertInstanceOf(FertilizerSensor.class, sensor);
      assertEquals("fertilizer-01", sensor.getDeviceId());
      assertEquals(SensorType.FERTILIZER, sensor.getDeviceType());
    }

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

  @Nested
  @DisplayName("Device ID Validation")
  class DeviceIdValidationTests {

    @Test
    @DisplayName("createSensor() trims whitespace from device ID")
    void testCreateSensorTrimsDeviceId() {
      Sensor sensor = SensorFactory.createSensor(SensorType.TEMPERATURE, "  temp-01  ");

      assertEquals("temp-01", sensor.getDeviceId());
    }

    @Test
    @DisplayName("createSensor() throws exception for null device ID")
    void testCreateSensorNullDeviceId() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(SensorType.TEMPERATURE, null);
      });

      assertTrue(exception.getMessage().contains("blank") ||
              exception.getMessage().contains("Device id"));
    }

    @Test
    @DisplayName("createSensor() throws exception for empty device ID")
    void testCreateSensorEmptyDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(SensorType.TEMPERATURE, "");
      });
    }

    @Test
    @DisplayName("createSensor() throws exception for blank device ID")
    void testCreateSensorBlankDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorFactory.createSensor(SensorType.TEMPERATURE, "   ");
      });
    }
  }

  @Nested
  @DisplayName("Sensor Type Validation")
  class SensorTypeValidationTests {

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

  @Nested
  @DisplayName("Created Sensor Properties")
  class SensorPropertiesTests {

    @Test
    @DisplayName("Created sensors have valid min/max ranges")
    void testCreatedSensorsHaveValidRanges() {
      for (SensorType type : SensorType.values()) {
        Sensor sensor = SensorFactory.createSensor(type, "test-01");

        assertTrue(sensor.getMinValue() < sensor.getMaxValue(),
                type + " should have min < max");
      }
    }

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