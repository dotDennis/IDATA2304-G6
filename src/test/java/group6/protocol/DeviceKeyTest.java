package group6.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the DeviceKey class.
 */
class DeviceKeyTest {

  @Nested
  @DisplayName("Factory Method - of()")
  class FactoryMethodTests {

    @Test
    @DisplayName("Create DeviceKey with valid type and id")
    void testOfWithValidTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Normalizes type to lowercase")
    void testOfNormalizesTypeToLowercase() {
      DeviceKey key = DeviceKey.of("TEMPERATURE", "sensor-01");

      assertEquals("temperature", key.getType());
    }

    @Test
    @DisplayName("Normalizes id to lowercase")
    void testOfNormalizesIdToLowercase() {
      DeviceKey key = DeviceKey.of("temperature", "SENSOR-01");

      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Trims whitespace from type")
    void testOfTrimsWhitespaceFromType() {
      DeviceKey key = DeviceKey.of("  temperature  ", "sensor-01");

      assertEquals("temperature", key.getType());
    }

    @Test
    @DisplayName("Trims whitespace from id")
    void testOfTrimsWhitespaceFromId() {
      DeviceKey key = DeviceKey.of("temperature", "  sensor-01  ");

      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Handles null type as empty string")
    void testOfWithNullType() {
      DeviceKey key = DeviceKey.of(null, "sensor-01");

      assertEquals("", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Handles null id as empty string")
    void testOfWithNullId() {
      DeviceKey key = DeviceKey.of("temperature", null);

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Handles both null type and id")
    void testOfWithBothNull() {
      DeviceKey key = DeviceKey.of(null, null);

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Handles blank type")
    void testOfWithBlankType() {
      DeviceKey key = DeviceKey.of("   ", "sensor-01");

      assertEquals("", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Handles blank id")
    void testOfWithBlankId() {
      DeviceKey key = DeviceKey.of("temperature", "   ");

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }
  }

  @Nested
  @DisplayName("Parse Method - parse()")
  class ParseMethodTests {

    @Test
    @DisplayName("Parses valid key with type and id")
    void testParseWithTypeAndId() {
      DeviceKey key = DeviceKey.parse("temperature#sensor-01");

      assertEquals("temperature", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Parses key with type only (no hash)")
    void testParseWithTypeOnly() {
      DeviceKey key = DeviceKey.parse("temperature");

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Parses key with empty id after hash")
    void testParseWithEmptyIdAfterHash() {
      DeviceKey key = DeviceKey.parse("temperature#");

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Returns empty DeviceKey for null input")
    void testParseWithNull() {
      DeviceKey key = DeviceKey.parse(null);

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Returns empty DeviceKey for empty string")
    void testParseWithEmptyString() {
      DeviceKey key = DeviceKey.parse("");

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Returns empty DeviceKey for blank string")
    void testParseWithBlankString() {
      DeviceKey key = DeviceKey.parse("   ");

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    @Test
    @DisplayName("Normalizes type to lowercase")
    void testParseNormalizesType() {
      DeviceKey key = DeviceKey.parse("TEMPERATURE#sensor-01");

      assertEquals("temperature", key.getType());
    }

    @Test
    @DisplayName("Trims whitespace from id")
    void testParseTrimsIdButNotLowercase() {
      DeviceKey key = DeviceKey.parse("temperature#  SENSOR-01  ");

      assertEquals("temperature", key.getType());
      assertEquals("SENSOR-01", key.getId());
    }

    @Test
    @DisplayName("Trims whitespace from entire input")
    void testParseTrimsWhitespace() {
      DeviceKey key = DeviceKey.parse("  temperature#sensor-01  ");

      assertEquals("temperature", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    @Test
    @DisplayName("Handles multiple hashes - uses first as delimiter")
    void testParseWithMultipleHashes() {
      DeviceKey key = DeviceKey.parse("temperature#sensor#01");

      assertEquals("temperature", key.getType());
      assertEquals("sensor#01", key.getId());
    }

    @Test
    @DisplayName("Handles hash at start")
    void testParseWithHashAtStart() {
      DeviceKey key = DeviceKey.parse("#sensor-01");

      assertEquals("", key.getType());
      assertEquals("sensor-01", key.getId());
    }
  }

  @Nested
  @DisplayName("Serialization - toProtocolKey()")
  class SerializationTests {

    @Test
    @DisplayName("Formats key with both type and id")
    void testToProtocolKeyWithTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature#sensor-01", key.toProtocolKey());
    }

    @Test
    @DisplayName("Formats key with type only (no id)")
    void testToProtocolKeyWithTypeOnly() {
      DeviceKey key = DeviceKey.of("temperature", "");

      assertEquals("temperature", key.toProtocolKey());
    }

    @Test
    @DisplayName("Formats key with null id as type only")
    void testToProtocolKeyWithNullId() {
      DeviceKey key = DeviceKey.of("temperature", null);

      assertEquals("temperature", key.toProtocolKey());
    }

    @Test
    @DisplayName("Formats key with blank id as type only")
    void testToProtocolKeyWithBlankId() {
      DeviceKey key = DeviceKey.of("temperature", "   ");

      assertEquals("temperature", key.toProtocolKey());
    }

    @Test
    @DisplayName("Trims whitespace from id in output")
    void testToProtocolKeyTrimsId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature#sensor-01", key.toProtocolKey());
    }
  }

  @Nested
  @DisplayName("Round-trip Tests")
  class RoundTripTests {

    @Test
    @DisplayName("Round-trip with type and id using of()")
    void testRoundTripOfWithTypeAndId() {
      DeviceKey original = DeviceKey.of("temperature", "sensor-01");
      String protocolKey = original.toProtocolKey();
      DeviceKey parsed = DeviceKey.parse(protocolKey);

      assertEquals(original.getType(), parsed.getType());
      // Note: parsed id is NOT normalized, but of() normalized it
      assertEquals("sensor-01", parsed.getId());
    }

    @Test
    @DisplayName("Round-trip with type only")
    void testRoundTripWithTypeOnly() {
      String original = "temperature";
      DeviceKey key = DeviceKey.parse(original);
      String result = key.toProtocolKey();

      assertEquals(original, result);
    }

    @Test
    @DisplayName("Round-trip parse -> toProtocolKey -> parse")
    void testParseToProtocolKeyRoundTrip() {
      String original = "temperature#sensor-01";
      DeviceKey key1 = DeviceKey.parse(original);
      String protocolKey = key1.toProtocolKey();
      DeviceKey key2 = DeviceKey.parse(protocolKey);

      assertEquals(key1.getType(), key2.getType());
      assertEquals(key1.getId(), key2.getId());
    }
  }

  @Nested
  @DisplayName("Equality Tests")
  class EqualityTests {

    @Test
    @DisplayName("Equal DeviceKeys with same type and id")
    void testEqualsWithSameValues() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1, key2);
    }

    @Test
    @DisplayName("Equal with self")
    void testEqualsWithSelf() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key, key);
    }

    @Test
    @DisplayName("Not equal with different type")
    void testNotEqualsWithDifferentType() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("humidity", "sensor-01");

      assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("Not equal with different id")
    void testNotEqualsWithDifferentId() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-02");

      assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("Not equal with null")
    void testNotEqualsWithNull() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertNotEquals(key, null);
    }

    @Test
    @DisplayName("Not equal with different class")
    void testNotEqualsWithDifferentClass() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      String notAKey = "temperature#sensor-01";

      assertNotEquals(key, notAKey);
    }

    @Test
    @DisplayName("Case-insensitive type comparison")
    void testEqualsCaseInsensitiveType() {
      DeviceKey key1 = DeviceKey.of("TEMPERATURE", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1, key2);
    }

    @Test
    @DisplayName("Case-sensitive id comparison (both via of)")
    void testEqualsCaseSensitiveIdViaOf() {
      DeviceKey key1 = DeviceKey.of("temperature", "SENSOR-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1, key2);  // Both normalized by of()
    }
  }

  @Nested
  @DisplayName("HashCode Tests")
  class HashCodeTests {

    @Test
    @DisplayName("Equal objects have equal hashCodes")
    void testHashCodeConsistency() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("Different objects have different hashCodes")
    void testHashCodeDifference() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("humidity", "sensor-01");

      assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("HashCode is consistent across calls")
    void testHashCodeConsistentAcrossCalls() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      int hash1 = key.hashCode();
      int hash2 = key.hashCode();

      assertEquals(hash1, hash2);
    }
  }

  @Nested
  @DisplayName("toString() Tests")
  class ToStringTests {

    @Test
    @DisplayName("toString with type and id")
    void testToStringWithTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature#sensor-01", key.toString());
    }

    @Test
    @DisplayName("toString with type only")
    void testToStringWithTypeOnly() {
      DeviceKey key = DeviceKey.of("temperature", "");

      assertEquals("temperature", key.toString());
    }

    @Test
    @DisplayName("toString matches toProtocolKey")
    void testToStringMatchesToProtocolKey() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key.toProtocolKey(), key.toString());
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Very long type")
    void testVeryLongType() {
      String longType = "type" + "x".repeat(100);
      DeviceKey key = DeviceKey.of(longType, "id");

      assertEquals(longType, key.getType());
    }

    @Test
    @DisplayName("Very long id")
    void testVeryLongId() {
      String longId = "id" + "x".repeat(100);
      DeviceKey key = DeviceKey.of("temperature", longId);

      assertEquals(longId, key.getId());
    }

    @Test
    @DisplayName("Special characters in type")
    void testSpecialCharactersInType() {
      DeviceKey key = DeviceKey.of("temp_sensor-01", "id");

      assertEquals("temp_sensor-01", key.getType());
    }

    @Test
    @DisplayName("Special characters in id")
    void testSpecialCharactersInId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor_01-A");

      assertEquals("sensor_01-a", key.getId());  // normalized
    }


    @Test
    @DisplayName("Multiple consecutive hashes in parse")
    void testMultipleConsecutiveHashes() {
      DeviceKey key = DeviceKey.parse("type###id");

      assertEquals("type", key.getType());
      assertEquals("##id", key.getId());
    }
  }
}
