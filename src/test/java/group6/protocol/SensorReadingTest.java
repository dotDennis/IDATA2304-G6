package group6.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the SensorReading class.
 */
class SensorReadingTest {

  @Nested
  @DisplayName("Factory Method - of()")
  class FactoryMethodTests {

    @Test
    @DisplayName("Create SensorReading with valid DeviceKey and value")
    void testOfWithValidInputs() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, 22.5);

      assertEquals(key, reading.getDeviceKey());
      assertEquals(22.5, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Create SensorReading with zero value")
    void testOfWithZeroValue() {
      DeviceKey key = DeviceKey.of("light", "sensor-01");
      SensorReading reading = SensorReading.of(key, 0.0);

      assertEquals(0.0, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Create SensorReading with negative value")
    void testOfWithNegativeValue() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, -5.2);

      assertEquals(-5.2, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Create SensorReading with large value")
    void testOfWithLargeValue() {
      DeviceKey key = DeviceKey.of("fertilizer", "sensor-01");
      SensorReading reading = SensorReading.of(key, 9999.99);

      assertEquals(9999.99, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Create SensorReading with very small decimal")
    void testOfWithSmallDecimal() {
      DeviceKey key = DeviceKey.of("ph", "sensor-01");
      SensorReading reading = SensorReading.of(key, 0.0001);

      assertEquals(0.0001, reading.getValue(), 0.00001);
    }

    @Test
    @DisplayName("Create SensorReading with DeviceKey without id")
    void testOfWithDeviceKeyWithoutId() {
      DeviceKey key = DeviceKey.of("temperature", "");
      SensorReading reading = SensorReading.of(key, 22.5);

      assertEquals(key, reading.getDeviceKey());
      assertEquals("", reading.getDeviceKey().getId());
    }
  }

  @Nested
  @DisplayName("Parse Method - parse()")
  class ParseMethodTests {

    @Test
    @DisplayName("Parse valid entry with type and id")
    void testParseWithTypeAndId() {
      SensorReading reading = SensorReading.parse("temperature#sensor-01:22.5");

      assertEquals("temperature", reading.getDeviceKey().getType());
      assertEquals("sensor-01", reading.getDeviceKey().getId());
      assertEquals(22.5, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Parse valid entry with type only")
    void testParseWithTypeOnly() {
      SensorReading reading = SensorReading.parse("temperature:22.5");

      assertEquals("temperature", reading.getDeviceKey().getType());
      assertEquals("", reading.getDeviceKey().getId());
      assertEquals(22.5, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Parse entry with negative value")
    void testParseWithNegativeValue() {
      SensorReading reading = SensorReading.parse("temperature:-5.2");

      assertEquals(-5.2, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Parse entry with zero value")
    void testParseWithZeroValue() {
      SensorReading reading = SensorReading.parse("light:0.0");

      assertEquals(0.0, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Parse entry with integer value")
    void testParseWithIntegerValue() {
      SensorReading reading = SensorReading.parse("humidity:65");

      assertEquals(65.0, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Parse entry with whitespace around value")
    void testParseWithWhitespaceAroundValue() {
      SensorReading reading = SensorReading.parse("temperature:  22.5  ");

      assertEquals(22.5, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Parse entry with scientific notation")
    void testParseWithScientificNotation() {
      SensorReading reading = SensorReading.parse("value:1.5e2");

      assertEquals(150.0, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Throws exception for null entry")
    void testParseWithNull() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SensorReading.parse(null);
      });
      assertTrue(exception.getMessage().contains("blank"));
    }

    @Test
    @DisplayName("Throws exception for empty string")
    void testParseWithEmptyString() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorReading.parse("");
      });
    }

    @Test
    @DisplayName("Throws exception for blank string")
    void testParseWithBlankString() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorReading.parse("   ");
      });
    }

    @Test
    @DisplayName("Throws exception for missing colon")
    void testParseWithMissingColon() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        SensorReading.parse("temperature22.5");
      });
      assertTrue(exception.getMessage().contains("Invalid"));
    }

    @Test
    @DisplayName("Throws exception for missing value")
    void testParseWithMissingValue() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorReading.parse("temperature:");
      });
    }

    @Test
    @DisplayName("Throws exception for invalid numeric value")
    void testParseWithInvalidValue() {
      assertThrows(NumberFormatException.class, () -> {
        SensorReading.parse("temperature:invalid");
      });
    }

    @Test
    @DisplayName("Throws exception for multiple colons")
    void testParseWithMultipleColons() {
      assertThrows(IllegalArgumentException.class, () -> {
        SensorReading.parse("temperature:22.5:extra");
      });
    }

    @Test
    @DisplayName("Parse handles whitespace in key part")
    void testParseWithWhitespaceInKey() {
      SensorReading reading = SensorReading.parse("  temperature#sensor-01  :22.5");

      assertEquals("temperature", reading.getDeviceKey().getType());
      assertEquals("sensor-01", reading.getDeviceKey().getId());
      assertEquals(22.5, reading.getValue(), 0.0001);
    }
  }

  @Nested
  @DisplayName("Serialization - toProtocolString()")
  class SerializationTests {

    @Test
    @DisplayName("Serialize reading with type and id")
    void testToProtocolStringWithTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, 22.5);

      assertEquals("temperature#sensor-01:22.5", reading.toProtocolString());
    }

    @Test
    @DisplayName("Serialize reading with type only")
    void testToProtocolStringWithTypeOnly() {
      DeviceKey key = DeviceKey.of("temperature", "");
      SensorReading reading = SensorReading.of(key, 22.5);

      assertEquals("temperature:22.5", reading.toProtocolString());
    }

    @Test
    @DisplayName("Serialize reading with negative value")
    void testToProtocolStringWithNegativeValue() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, -5.2);

      assertEquals("temperature#sensor-01:-5.2", reading.toProtocolString());
    }

    @Test
    @DisplayName("Serialize reading with zero value")
    void testToProtocolStringWithZeroValue() {
      DeviceKey key = DeviceKey.of("light", "sensor-01");
      SensorReading reading = SensorReading.of(key, 0.0);

      assertEquals("light#sensor-01:0.0", reading.toProtocolString());
    }

    @Test
    @DisplayName("Serialize reading with large value")
    void testToProtocolStringWithLargeValue() {
      DeviceKey key = DeviceKey.of("fertilizer", "sensor-01");
      SensorReading reading = SensorReading.of(key, 12345.6789);

      assertEquals("fertilizer#sensor-01:12345.6789", reading.toProtocolString());
    }

    @Test
    @DisplayName("Serialize reading with integer-like value")
    void testToProtocolStringWithIntegerValue() {
      DeviceKey key = DeviceKey.of("humidity", "sensor-01");
      SensorReading reading = SensorReading.of(key, 65.0);

      assertEquals("humidity#sensor-01:65.0", reading.toProtocolString());
    }
  }

  @Nested
  @DisplayName("Round-trip Tests")
  class RoundTripTests {

    @Test
    @DisplayName("Round-trip with type and id")
    void testRoundTripWithTypeAndId() {
      String original = "temperature#sensor-01:22.5";
      SensorReading reading = SensorReading.parse(original);
      String result = reading.toProtocolString();

      assertEquals(original, result);
    }

    @Test
    @DisplayName("Round-trip with type only")
    void testRoundTripWithTypeOnly() {
      String original = "temperature:22.5";
      SensorReading reading = SensorReading.parse(original);
      String result = reading.toProtocolString();

      assertEquals(original, result);
    }

    @Test
    @DisplayName("Round-trip with negative value")
    void testRoundTripWithNegativeValue() {
      String original = "temperature:-5.2";
      SensorReading reading = SensorReading.parse(original);
      String result = reading.toProtocolString();

      assertEquals(original, result);
    }

    @Test
    @DisplayName("Round-trip of() -> toProtocolString() -> parse()")
    void testRoundTripOfToProtocolStringParse() {
      DeviceKey key = DeviceKey.of("humidity", "sensor-01");
      SensorReading original = SensorReading.of(key, 65.5);

      String protocolString = original.toProtocolString();
      SensorReading parsed = SensorReading.parse(protocolString);

      assertEquals(original.getDeviceKey().getType(), parsed.getDeviceKey().getType());
      assertEquals(original.getDeviceKey().getId(), parsed.getDeviceKey().getId());
      assertEquals(original.getValue(), parsed.getValue(), 0.0001);
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Very small decimal value")
    void testVerySmallDecimal() {
      DeviceKey key = DeviceKey.of("ph", "sensor-01");
      SensorReading reading = SensorReading.of(key, 0.000001);

      assertEquals(0.000001, reading.getValue(), 0.0000001);
    }

    @Test
    @DisplayName("Very large value")
    void testVeryLargeValue() {
      DeviceKey key = DeviceKey.of("fertilizer", "sensor-01");
      SensorReading reading = SensorReading.of(key, 999999.999999);

      assertEquals(999999.999999, reading.getValue(), 0.000001);
    }

    @Test
    @DisplayName("Value with many decimal places")
    void testManyDecimalPlaces() {
      String input = "temperature:22.123456789";
      SensorReading reading = SensorReading.parse(input);

      assertEquals(22.123456789, reading.getValue(), 0.0000000001);
    }

    @Test
    @DisplayName("Parse preserves DeviceKey normalization")
    void testParsePreservesKeyNormalization() {
      SensorReading reading = SensorReading.parse("TEMPERATURE#SENSOR-01:22.5");

      assertEquals("temperature", reading.getDeviceKey().getType());
      assertEquals("SENSOR-01", reading.getDeviceKey().getId());
    }

    @Test
    @DisplayName("Multiple sensor readings format")
    void testMultipleSensorReadingsFormat() {
      String[] readings = {
              "temperature#temp-01:22.5",
              "humidity#humid-01:65.0",
              "light#light-01:450.0"
      };

      for (String readingStr : readings) {
        SensorReading reading = SensorReading.parse(readingStr);
        assertNotNull(reading);
        assertEquals(readingStr, reading.toProtocolString());
      }
    }

    @Test
    @DisplayName("Decimal precision is preserved")
    void testDecimalPrecisionPreserved() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, 22.123456789012345);

      assertEquals(22.123456789012345, reading.getValue(), 0.0000000000000001);
    }
  }

  @Nested
  @DisplayName("Getter Tests")
  class GetterTests {

    @Test
    @DisplayName("getDeviceKey returns correct DeviceKey")
    void testGetDeviceKey() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, 22.5);

      assertEquals(key, reading.getDeviceKey());
      assertSame(key, reading.getDeviceKey());
    }

    @Test
    @DisplayName("getValue returns correct value")
    void testGetValue() {
      DeviceKey key = DeviceKey.of("humidity", "sensor-01");
      SensorReading reading = SensorReading.of(key, 65.5);

      assertEquals(65.5, reading.getValue(), 0.0001);
    }

    @Test
    @DisplayName("Getters are consistent across calls")
    void testGettersConsistent() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      SensorReading reading = SensorReading.of(key, 22.5);

      assertEquals(reading.getDeviceKey(), reading.getDeviceKey());
      assertEquals(reading.getValue(), reading.getValue(), 0.0001);
    }
  }
}