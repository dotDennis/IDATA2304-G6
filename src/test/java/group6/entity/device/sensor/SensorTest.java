package group6.entity.device.sensor;

import group6.entity.device.SensorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential unit tests for Sensor implementations.
 *
 *Tests verify:
 *
 * Sensor creation for all types (Temperature, Humidity, Light, pH, Wind, Fertilizer)
 *Value reading and bounds enforcement
 *Manual adjustment with clamping to min/max values
 *Update interval configuration
 */
class SensorTest {

  /**
   * Tests for sensor creation and initialization.
   */
  @Nested
  @DisplayName("Sensor Creation")
  class CreationTests {


    /**
     * Verifies that all sensor types can be instantiated with valid properties.
     *
     *Checks that each sensor has:
     *
     *A non-null device ID
     *A non-null device type
     *Valid min/max range where min &lt; max
     *
     */
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

    /**
     * Verifies TemperatureSensor properties are correctly initialized.
     *
     *Expected values:
     *
     *Device ID: "temp-01"
     *Type: TEMPERATURE
     *Min: 10.0°C
     *Max: 35.0°C
     */
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

  /**
   * Tests for sensor value reading.
   */
  @Nested
  @DisplayName("Reading Values")
  class ReadValueTests {

    /**
     * Verifies readValue() always returns values within the sensor's valid range.
     *
     * Performs 50 successive reads to ensure consistent bounds enforcement.
     */
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

    /**
     * Verifies getCurrentValue() initializes to the midpoint of the sensor's range.
     *
     *For a temperature sensor with range [10.0, 35.0], the initial value
     *should be 22.5°C.
     */
    @Test
    @DisplayName("getCurrentValue() initializes to mid-range")
    void testGetCurrentValueInitializes() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      double value = sensor.getCurrentValue();
      double expectedMid = (sensor.getMinValue() + sensor.getMaxValue()) / 2.0;

      assertEquals(expectedMid, value, 0.001);
    }
  }

  /**
   * Tests for manual sensor value adjustment.
   */
  @Nested
  @DisplayName("Manual Adjustment")
  class ManualAdjustTests {

    /**
     * Verifies manualAdjust() can increase and decrease sensor values.
     *
     *Tests positive and negative adjustments to ensure the value changes
     *in the expected direction.
     */
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

    /**
     * Verifies manualAdjust() clamps values to the sensor's min/max bounds.
     *
     *Tests that:
     *Large negative adjustments clamp to minValue
     *Large positive adjustments clamp to maxValue
     */
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

  /**
   * Tests for sensor update interval configuration.
   */
  @Nested
  @DisplayName("Update Interval")
  class IntervalTests {

    /**
     * Verifies the default update interval is 5000 milliseconds.
     */
    @Test
    @DisplayName("Default interval is 5000ms")
    void testDefaultInterval() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      assertEquals(5000L, sensor.getUpdateInterval());
    }

    /**
     * Verifies setUpdateInterval() rejects invalid values and uses the default.
     *
     *Invalid values include
     * Zero
     * Negative numbers
     * In all cases, the interval should remain at the default 5000ms.
     */

    @Test
    @DisplayName("setUpdateInterval() with invalid values uses default")
    void testInvalidInterval() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      sensor.setUpdateInterval(0L);
      assertEquals(5000L, sensor.getUpdateInterval());

      sensor.setUpdateInterval(-100L);
      assertEquals(5000L, sensor.getUpdateInterval());
    }

    /**
     * Verifies setUpdateInterval() accepts and stores valid positive values.
     */
    @Test
    @DisplayName("setUpdateInterval() accepts valid values")
    void testValidInterval() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      sensor.setUpdateInterval(10000L);
      assertEquals(10000L, sensor.getUpdateInterval());
    }
  }
}