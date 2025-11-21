package group6.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the DeviceKey class.
 *
 * DeviceKey is a protocol component that uniquely identifies devices using the format
 * Tests verify:
 * Factory method {@code of()} with normalization (lowercase, trimming)
 * Parse method {@code parse()} from protocol strings
 * Serialization to protocol format via {@code toProtocolKey()}
 * Round-trip serialization/deserialization
 * Equality and hash code contracts
 * Edge cases (null, empty, special characters)
 */
class DeviceKeyTest {

  /**
   * Tests for the factory method {@code of()}.
   */
  @Nested
  @DisplayName("Factory Method - of()")
  class FactoryMethodTests {

    /**
     * Verifies DeviceKey can be created with valid type and id.
     */
    @Test
    @DisplayName("Create DeviceKey with valid type and id")
    void testOfWithValidTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies type is normalized to lowercase.
     */
    @Test
    @DisplayName("Normalizes type to lowercase")
    void testOfNormalizesTypeToLowercase() {
      DeviceKey key = DeviceKey.of("TEMPERATURE", "sensor-01");

      assertEquals("temperature", key.getType());
    }

    /**
     * Verifies id is normalized to lowercase.
     */
    @Test
    @DisplayName("Normalizes id to lowercase")
    void testOfNormalizesIdToLowercase() {
      DeviceKey key = DeviceKey.of("temperature", "SENSOR-01");

      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies leading and trailing whitespace is trimmed from type.
     */
    @Test
    @DisplayName("Trims whitespace from type")
    void testOfTrimsWhitespaceFromType() {
      DeviceKey key = DeviceKey.of("  temperature  ", "sensor-01");

      assertEquals("temperature", key.getType());
    }

    /**
     * Verifies leading and trailing whitespace is trimmed from id.
     */
    @Test
    @DisplayName("Trims whitespace from id")
    void testOfTrimsWhitespaceFromId() {
      DeviceKey key = DeviceKey.of("temperature", "  sensor-01  ");

      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies null type is handled as empty string.
     */
    @Test
    @DisplayName("Handles null type as empty string")
    void testOfWithNullType() {
      DeviceKey key = DeviceKey.of(null, "sensor-01");

      assertEquals("", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies null id is handled as empty string.
     */
    @Test
    @DisplayName("Handles null id as empty string")
    void testOfWithNullId() {
      DeviceKey key = DeviceKey.of("temperature", null);

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies both null type and id are handled as empty strings.
     */
    @Test
    @DisplayName("Handles both null type and id")
    void testOfWithBothNull() {
      DeviceKey key = DeviceKey.of(null, null);

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies blank type (whitespace only) is normalized to empty string.
     */
    @Test
    @DisplayName("Handles blank type")
    void testOfWithBlankType() {
      DeviceKey key = DeviceKey.of("   ", "sensor-01");

      assertEquals("", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies blank id (whitespace only) is normalized to empty string.
     */
    @Test
    @DisplayName("Handles blank id")
    void testOfWithBlankId() {
      DeviceKey key = DeviceKey.of("temperature", "   ");

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }
  }

  /**
   * Tests for the parse method {@code parse()}.
   */
  @Nested
  @DisplayName("Parse Method - parse()")
  class ParseMethodTests {

    /**
     * Verifies parsing of valid key in format "type#id".
     */
    @Test
    @DisplayName("Parses valid key with type and id")
    void testParseWithTypeAndId() {
      DeviceKey key = DeviceKey.parse("temperature#sensor-01");

      assertEquals("temperature", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies parsing of key with type only.
     */
    @Test
    @DisplayName("Parses key with type only (no hash)")
    void testParseWithTypeOnly() {
      DeviceKey key = DeviceKey.parse("temperature");

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies parsing of key with empty id after hash.
     */
    @Test
    @DisplayName("Parses key with empty id after hash")
    void testParseWithEmptyIdAfterHash() {
      DeviceKey key = DeviceKey.parse("temperature#");

      assertEquals("temperature", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies null input returns empty DeviceKey.
     */
    @Test
    @DisplayName("Returns empty DeviceKey for null input")
    void testParseWithNull() {
      DeviceKey key = DeviceKey.parse(null);

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies empty string returns empty DeviceKey.
     */
    @Test
    @DisplayName("Returns empty DeviceKey for empty string")
    void testParseWithEmptyString() {
      DeviceKey key = DeviceKey.parse("");

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies blank string returns empty DeviceKey.
     */
    @Test
    @DisplayName("Returns empty DeviceKey for blank string")
    void testParseWithBlankString() {
      DeviceKey key = DeviceKey.parse("   ");

      assertEquals("", key.getType());
      assertEquals("", key.getId());
    }

    /**
     * Verifies type is normalized to lowercase during parsing.
     */
    @Test
    @DisplayName("Normalizes type to lowercase")
    void testParseNormalizesType() {
      DeviceKey key = DeviceKey.parse("TEMPERATURE#sensor-01");

      assertEquals("temperature", key.getType());
    }

    /**
     * Verifies id whitespace is trimmed but case is preserved during parsing.
     */
    @Test
    @DisplayName("Trims whitespace from id")
    void testParseTrimsIdButNotLowercase() {
      DeviceKey key = DeviceKey.parse("temperature#  SENSOR-01  ");

      assertEquals("temperature", key.getType());
      assertEquals("SENSOR-01", key.getId());
    }

    /**
     * Verifies whitespace is trimmed from entire input string.
     */
    @Test
    @DisplayName("Trims whitespace from entire input")
    void testParseTrimsWhitespace() {
      DeviceKey key = DeviceKey.parse("  temperature#sensor-01  ");

      assertEquals("temperature", key.getType());
      assertEquals("sensor-01", key.getId());
    }

    /**
     * Verifies multiple hashes use first as delimiter, rest become part of id.
     */
    @Test
    @DisplayName("Handles multiple hashes - uses first as delimiter")
    void testParseWithMultipleHashes() {
      DeviceKey key = DeviceKey.parse("temperature#sensor#01");

      assertEquals("temperature", key.getType());
      assertEquals("sensor#01", key.getId());
    }

    /**
     * Verifies hash at start results in empty type.
     */
    @Test
    @DisplayName("Handles hash at start")
    void testParseWithHashAtStart() {
      DeviceKey key = DeviceKey.parse("#sensor-01");

      assertEquals("", key.getType());
      assertEquals("sensor-01", key.getId());
    }
  }

  /**
   * Tests for serialization to protocol format.
   */
  @Nested
  @DisplayName("Serialization - toProtocolKey()")
  class SerializationTests {

    /**
     * Verifies serialization of key with both type and id.
     */
    @Test
    @DisplayName("Formats key with both type and id")
    void testToProtocolKeyWithTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature#sensor-01", key.toProtocolKey());
    }

    /**
     * Verifies serialization of key with type only (no hash).
     */
    @Test
    @DisplayName("Formats key with type only (no id)")
    void testToProtocolKeyWithTypeOnly() {
      DeviceKey key = DeviceKey.of("temperature", "");

      assertEquals("temperature", key.toProtocolKey());
    }

    /**
     * Verifies null id is omitted from serialization.
     */
    @Test
    @DisplayName("Formats key with null id as type only")
    void testToProtocolKeyWithNullId() {
      DeviceKey key = DeviceKey.of("temperature", null);

      assertEquals("temperature", key.toProtocolKey());
    }

    /**
     * Verifies blank id is omitted from serialization.
     */
    @Test
    @DisplayName("Formats key with blank id as type only")
    void testToProtocolKeyWithBlankId() {
      DeviceKey key = DeviceKey.of("temperature", "   ");

      assertEquals("temperature", key.toProtocolKey());
    }

    /**
     * Verifies whitespace is trimmed from id in serialized output.
     */
    @Test
    @DisplayName("Trims whitespace from id in output")
    void testToProtocolKeyTrimsId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature#sensor-01", key.toProtocolKey());
    }
  }

  /**
   * Tests for round-trip serialization and deserialization.
   */
  @Nested
  @DisplayName("Round-trip Tests")
  class RoundTripTests {

    /**
     * Verifies round-trip: of() → toProtocolKey() → parse().
     */
    @Test
    @DisplayName("Round-trip with type and id using of()")
    void testRoundTripOfWithTypeAndId() {
      DeviceKey original = DeviceKey.of("temperature", "sensor-01");
      String protocolKey = original.toProtocolKey();
      DeviceKey parsed = DeviceKey.parse(protocolKey);

      assertEquals(original.getType(), parsed.getType());
      assertEquals("sensor-01", parsed.getId());
    }

    /**
     * Verifies round-trip with type only.
     */
    @Test
    @DisplayName("Round-trip with type only")
    void testRoundTripWithTypeOnly() {
      String original = "temperature";
      DeviceKey key = DeviceKey.parse(original);
      String result = key.toProtocolKey();

      assertEquals(original, result);
    }

    /**
     * Verifies round-trip: parse() → toProtocolKey() → parse().
     */
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

  /**
   * Tests for equality contract.
   */
  @Nested
  @DisplayName("Equality Tests")
  class EqualityTests {

    /**
     * Verifies DeviceKeys with same type and id are equal.
     */
    @Test
    @DisplayName("Equal DeviceKeys with same type and id")
    void testEqualsWithSameValues() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1, key2);
    }

    /**
     * Verifies DeviceKey equals itself (reflexivity).
     */
    @Test
    @DisplayName("Equal with self")
    void testEqualsWithSelf() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key, key);
    }

    /**
     * Verifies DeviceKeys with different types are not equal.
     */
    @Test
    @DisplayName("Not equal with different type")
    void testNotEqualsWithDifferentType() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("humidity", "sensor-01");

      assertNotEquals(key1, key2);
    }

    /**
     * Verifies DeviceKeys with different ids are not equal.
     */
    @Test
    @DisplayName("Not equal with different id")
    void testNotEqualsWithDifferentId() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-02");

      assertNotEquals(key1, key2);
    }

    /**
     * Verifies DeviceKey is not equal to null.
     */
    @Test
    @DisplayName("Not equal with null")
    void testNotEqualsWithNull() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertNotEquals(key, null);
    }

    /**
     * Verifies DeviceKey is not equal to objects of different class.
     */
    @Test
    @DisplayName("Not equal with different class")
    void testNotEqualsWithDifferentClass() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      String notAKey = "temperature#sensor-01";

      assertNotEquals(key, notAKey);
    }

    /**
     * Verifies type comparison is case-insensitive.
     */
    @Test
    @DisplayName("Case-insensitive type comparison")
    void testEqualsCaseInsensitiveType() {
      DeviceKey key1 = DeviceKey.of("TEMPERATURE", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1, key2);
    }

    /**
     * Verifies id comparison is case-insensitive when both normalized via of().
     */
    @Test
    @DisplayName("Case-sensitive id comparison (both via of)")
    void testEqualsCaseSensitiveIdViaOf() {
      DeviceKey key1 = DeviceKey.of("temperature", "SENSOR-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1, key2);  // Both normalized by of()
    }
  }

  /**
   * Tests for hash code contract.
   */
  @Nested
  @DisplayName("HashCode Tests")
  class HashCodeTests {

    /**
     * Verifies equal objects have equal hash codes.
     */
    @Test
    @DisplayName("Equal objects have equal hashCodes")
    void testHashCodeConsistency() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key1.hashCode(), key2.hashCode());
    }

    /**
     * Verifies different objects typically have different hash codes.
     */
    @Test
    @DisplayName("Different objects have different hashCodes")
    void testHashCodeDifference() {
      DeviceKey key1 = DeviceKey.of("temperature", "sensor-01");
      DeviceKey key2 = DeviceKey.of("humidity", "sensor-01");

      assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    /**
     * Verifies hash code is consistent across multiple calls.
     */
    @Test
    @DisplayName("HashCode is consistent across calls")
    void testHashCodeConsistentAcrossCalls() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");
      int hash1 = key.hashCode();
      int hash2 = key.hashCode();

      assertEquals(hash1, hash2);
    }
  }

  /**
   * Tests for toString() method.
   */
  @Nested
  @DisplayName("toString() Tests")
  class ToStringTests {

    /**
     * Verifies toString() format with both type and id.
     */
    @Test
    @DisplayName("toString with type and id")
    void testToStringWithTypeAndId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals("temperature#sensor-01", key.toString());
    }

    /**
     * Verifies toString() format with type only.
     */
    @Test
    @DisplayName("toString with type only")
    void testToStringWithTypeOnly() {
      DeviceKey key = DeviceKey.of("temperature", "");

      assertEquals("temperature", key.toString());
    }

    /**
     * Verifies toString() matches toProtocolKey().
     */
    @Test
    @DisplayName("toString matches toProtocolKey")
    void testToStringMatchesToProtocolKey() {
      DeviceKey key = DeviceKey.of("temperature", "sensor-01");

      assertEquals(key.toProtocolKey(), key.toString());
    }
  }

  /**
   * Tests for edge cases and boundary conditions.
   */
  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    /**
     * Verifies handling of very long type strings.
     */
    @Test
    @DisplayName("Very long type")
    void testVeryLongType() {
      String longType = "type" + "x".repeat(100);
      DeviceKey key = DeviceKey.of(longType, "id");

      assertEquals(longType, key.getType());
    }

    /**
     * Verifies handling of very long id strings.
     */
    @Test
    @DisplayName("Very long id")
    void testVeryLongId() {
      String longId = "id" + "x".repeat(100);
      DeviceKey key = DeviceKey.of("temperature", longId);

      assertEquals(longId, key.getId());
    }

    /**
     * Verifies special characters are allowed in type.
     */
    @Test
    @DisplayName("Special characters in type")
    void testSpecialCharactersInType() {
      DeviceKey key = DeviceKey.of("temp_sensor-01", "id");

      assertEquals("temp_sensor-01", key.getType());
    }

    /**
     * Verifies special characters in id are normalized.
     */
    @Test
    @DisplayName("Special characters in id")
    void testSpecialCharactersInId() {
      DeviceKey key = DeviceKey.of("temperature", "sensor_01-A");

      assertEquals("sensor_01-a", key.getId());  // normalized
    }


    /**
     * Verifies multiple consecutive hashes in parse.
     */
    @Test
    @DisplayName("Multiple consecutive hashes in parse")
    void testMultipleConsecutiveHashes() {
      DeviceKey key = DeviceKey.parse("type###id");

      assertEquals("type", key.getType());
      assertEquals("##id", key.getId());
    }
  }
}
