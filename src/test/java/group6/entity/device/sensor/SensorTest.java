package group6.entity.device.sensor;

import group6.entity.device.SensorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential unit tests for Sensor implementations.
 */
class SensorTest {

  @Nested
  @DisplayName("Sensor Creation")
  class CreationTests {

    @Test
    @DisplayName("Create all sensor types")
    void testCreateAllSensorTypes() {
      Sensor[] sensors = {
              new TemperatureSensor("temp-01"),
              new HumiditySensor("humid-01"),
              new LightSensor("light-01"),
              new PhSensor("ph-01"),
              new WindSensor("wind-01"),
              new FertilizerSensor("fertilizer-01")
      };

      for (Sensor sensor : sensors) {
        assertNotNull(sensor.getDeviceId());
        assertNotNull(sensor.getDeviceType());
        assertTrue(sensor.getMinValue() < sensor.getMaxValue());
      }
    }

    @Test
    @DisplayName("TemperatureSensor has correct properties")
    void testTemperatureSensor() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      assertEquals("temp-01", sensor.getDeviceId());
      assertEquals(SensorType.TEMPERATURE, sensor.getDeviceType());
      assertEquals(10.0, sensor.getMinValue(), 0.001);
      assertEquals(35.0, sensor.getMaxValue(), 0.001);
    }
  }

  @Nested
  @DisplayName("Reading Values")
  class ReadValueTests {

    @Test
    @DisplayName("readValue() stays within bounds")
    void testReadValueWithinBounds() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      for (int i = 0; i < 50; i++) {
        double value = sensor.readValue();
        assertTrue(value >= sensor.getMinValue());
        assertTrue(value <= sensor.getMaxValue());
      }
    }

    @Test
    @DisplayName("getCurrentValue() initializes to mid-range")
    void testGetCurrentValueInitializes() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      double value = sensor.getCurrentValue();
      double expectedMid = (sensor.getMinValue() + sensor.getMaxValue()) / 2.0;

      assertEquals(expectedMid, value, 0.001);
    }
  }

  @Nested
  @DisplayName("Manual Adjustment")
  class ManualAdjustTests {

    @Test
    @DisplayName("manualAdjust() increases/decreases value")
    void testManualAdjust() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      double initial = sensor.getCurrentValue();

      sensor.manualAdjust(5.0);
      assertTrue(sensor.getCurrentValue() >= initial);

      sensor.manualAdjust(-10.0);
      assertTrue(sensor.getCurrentValue() < sensor.getMaxValue());
    }

    @Test
    @DisplayName("manualAdjust() respects min/max bounds")
    void testManualAdjustBounds() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      sensor.manualAdjust(-10000.0);
      assertEquals(sensor.getMinValue(), sensor.getCurrentValue(), 0.001);

      sensor.manualAdjust(10000.0);
      assertEquals(sensor.getMaxValue(), sensor.getCurrentValue(), 0.001);
    }
  }

  @Nested
  @DisplayName("Update Interval")
  class IntervalTests {

    @Test
    @DisplayName("Default interval is 5000ms")
    void testDefaultInterval() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      assertEquals(5000L, sensor.getUpdateInterval());
    }

    @Test
    @DisplayName("setUpdateInterval() with invalid values uses default")
    void testInvalidInterval() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      sensor.setUpdateInterval(0L);
      assertEquals(5000L, sensor.getUpdateInterval());

      sensor.setUpdateInterval(-100L);
      assertEquals(5000L, sensor.getUpdateInterval());
    }

    @Test
    @DisplayName("setUpdateInterval() accepts valid values")
    void testValidInterval() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      sensor.setUpdateInterval(10000L);
      assertEquals(10000L, sensor.getUpdateInterval());
    }
  }
}