package group6.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Message class
 *
 * Message is the core protocol component for client-server communication using the format
 * {@code TYPE|nodeId|data}.
 * Tests verify:
 * Constructor validation (null/empty nodeId, null messageType)
 * Serialization to protocol format via {@code toProtocolString()}
 * Deserialization from protocol format via {@code fromProtocolString()}
 * Round-trip serialization/deserialization
 * All MessageType enum values (HELLO, WELCOME, DATA, COMMAND, SUCCESS, FAILURE, ERROR, KEEPALIVE)
 * Edge cases (long strings, Unicode, special characters, pipes in data)
 */
public class MessageTest {

  /**
   * Tests for Message constructor validation.
   */
  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    /**
     * Verifies Message can be created with all fields populated.
     */
    @Test
    @DisplayName ("Create message with all fields populated")
    void testCreateMessageWithAllFields() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "temperature: 22.5");

      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("temperature: 22.5", message.getData());
    }

    /**
     * Verifies Message can be created with empty data string.
     */
    @Test
    @DisplayName("Create message with empty data string")
    void testCreateMessageWithEmptyData() {
      Message message = new Message(MessageType.HELLO, "control-01", "");

      assertEquals(MessageType.HELLO, message.getMessageType());
      assertEquals("control-01", message.getNodeId());
      assertEquals("", message.getData());
    }

    /**
     * Verifies null data is converted to empty string.
     */
    @Test
    @DisplayName("Null data is converted to empty string")
    void testNullDataConvertsToEmptyString() {
      Message message = new Message(MessageType.KEEPALIVE, "sensor-01", null);

      assertEquals("", message.getData());
    }

    /**
     * Verifies null nodeId throws IllegalArgumentException.
     */
    @Test
    @DisplayName("Null nodeId thows IllegalargumentException")
    void testNullNodeIdThrowsException() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Message(MessageType.DATA, null, "data");
      });

      String errorMessage = exception.getMessage();
      assertTrue(errorMessage.contains("nodeId") || errorMessage.contains("null") ||
              errorMessage.contains("blank"));
    }

    /**
     * Verifies blank nodeId throws IllegalArgumentException.
     */
    @Test
    @DisplayName("Blank nodeId throws IllegalArgumentException")
    void testBlankNodeIdThrowsException() {
      assertThrows(IllegalArgumentException.class, () -> {
        new Message(MessageType.DATA, "   ", "data");
      });
    }

    /**
     * Verifies empty nodeId throws IllegalArgumentException.
     */
    @Test
    @DisplayName("Empty nodeId throws IllegalArgumentException")
    void testEmptyNodeIdThrowsException() {
      assertThrows(IllegalArgumentException.class, () -> {
        new Message(MessageType.DATA, "", "data");
      });
    }

    /**
     * Verifies null messageType throws NullPointerException.
     */
    @Test
    @DisplayName("Null messageType throws NullPointerException")
    void testNullMessageTypeThrowsException() {
      assertThrows(NullPointerException.class, () -> {
        new Message(null, "sensor-01", "data");
      });
    }
  }

  /**
   * Tests for serialization to protocol format.
   */
  @Nested
  @DisplayName("Serialization Tests - toProtocolString()")
  class SerializationTests {

    /**
     * Verifies message with data is formatted correctly.
     * Expected format: {@code TYPE|nodeId|data}
     */
    @Test
    @DisplayName("Formats message with data correctly")
    void testToProtocolStringWithData() {
      Message message = new Message(MessageType.DATA, "sensor-01", "temperature:22.5");

      assertEquals("DATA|sensor-01|temperature:22.5", message.toProtocolString());
    }

    /**
     * Verifies message without data omits the data field.
     * Expected format: {@code TYPE|nodeId}
     */
    @Test
    @DisplayName("Formats message without data correctly")
    void testToProtocolStringWithoutData() {
      Message message = new Message(MessageType.HELLO, "control-01", "");

      assertEquals("HELLO|control-01", message.toProtocolString());
    }

    /**
     * Verifies null data is handled as empty (no data field in output).
     */
    @Test
    @DisplayName("Handles null data as empty (no data field)")
    void testToProtocolStringWithNullData() {
      Message message = new Message(MessageType.KEEPALIVE, "sensor-01", null);

      assertEquals("KEEPALIVE|sensor-01", message.toProtocolString());
    }

    /**
     * Verifies pipe characters in data are preserved.
     */
    @Test
    @DisplayName("Includes data with pipe characters")
    void testToProtocolStringWithPipeInData() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "key1:value1|key2:value2");

      assertEquals("DATA|sensor-01|key1:value1|key2:value2", message.toProtocolString());
    }

    /**
     * Verifies whitespace in data field is preserved.
     */
    @Test
    @DisplayName("Preserves whitespace in data field")
    void testToProtocolStringPreservesDataWhitespace() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "  data with spaces  ");

      assertEquals("DATA|sensor-01|  data with spaces  ", message.toProtocolString());
    }

    /**
     * Verifies special characters in data are preserved.
     */
    @Test
    @DisplayName("Includes special characters in data")
    void testToProtocolStringWithSpecialCharacters() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "temp:22.5°C,status:OK✓");

      assertEquals("DATA|sensor-01|temp:22.5°C,status:OK✓", message.toProtocolString());
    }
  }

  /**
   * Tests for deserialization from protocol format.
   */
  @Nested
  @DisplayName("Deserialization Tests - fromProtocolString()")
  class DeserializationTests {

    /**
     * Verifies valid message with data can be parsed.
     */
    @Test
    @DisplayName("Parses valid message with data")
    void testFromProtocolStringWithData() {
      Message message = Message.fromProtocolString("DATA|sensor-01|temperature:22.5");

      assertNotNull(message);
      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("temperature:22.5", message.getData());
    }

    /**
     * Verifies valid message without data field can be parsed.
     */
    @Test
    @DisplayName("Parses valid message without data field")
    void testFromProtocolStringWithoutData() {
      Message message = Message.fromProtocolString("HELLO|control-01");

      assertNotNull(message);
      assertEquals(MessageType.HELLO, message.getMessageType());
      assertEquals("control-01", message.getNodeId());
      assertEquals("", message.getData());
    }

    /**
     * Verifies message with empty data field is parsed correctly.
     */
    @Test
    @DisplayName("Parses message with empty data field")
    void testFromProtocolStringWithEmptyDataField() {
      Message message = Message.fromProtocolString("KEEPALIVE|sensor-01|");

      assertNotNull(message);
      assertEquals(MessageType.KEEPALIVE, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("", message.getData());
    }

    /**
     * Verifies data containing pipe characters is parsed correctly.
     * Uses split limit of 3 to preserve pipes in data field.
     */
    @Test
    @DisplayName("Handles data with pipe characters (split limit 3)")
    void testFromProtocolStringDataWithPipes() {
      Message message = Message.fromProtocolString("DATA|sensor-01|key1:value1|key2:value2");

      assertNotNull(message);
      assertEquals("key1:value1|key2:value2", message.getData());
    }

    /**
     * Verifies whitespace in data field is preserved.
     */
    @Test
    @DisplayName("Preserves whitespace in data field")
    void testFromProtocolStringPreservesDataWhitespace() {
      Message message = Message.fromProtocolString("DATA|sensor-01|  data with spaces  ");

      assertNotNull(message);
      assertEquals("  data with spaces  ", message.getData());
    }

    /**
     * Verifies whitespace is trimmed from outer protocol string.
     */
    @Test
    @DisplayName("Trims whitespace from outer protocol string")
    void testFromProtocolStringTrimsOuterWhitespace() {
      Message message = Message.fromProtocolString("  DATA|sensor-01|temperature:22.5");

      assertNotNull(message);
      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("temperature:22.5", message.getData());
    }

    /**
     * Verifies whitespace is trimmed from messageType field.
     */
    @Test
    @DisplayName("Trims whitespace from messageType")
    void testFromProtocolStringTrimsMessageType() {
      Message message = Message.fromProtocolString("  DATA  |sensor-01|temperature:22.5");

      assertNotNull(message);
      assertEquals(MessageType.DATA, message.getMessageType());
    }

    /**
     * Verifies whitespace is trimmed from nodeId field.
     */
    @Test
    @DisplayName("Trims whitespace from nodeId")
    void testFromProtocolStringTrimsNodeId() {
      Message message = Message.fromProtocolString("DATA|  sensor-01  |temperature:22.5");

      assertNotNull(message);
      assertEquals("sensor-01", message.getNodeId());
    }

    /**
     * Verifies null input returns null.
     */
    @Test
    @DisplayName("Returns null for null input")
    void testFromProtocolStringWithNull() {
      assertNull(Message.fromProtocolString(null));
    }

    /**
     * Verifies empty string returns null.
     */
    @Test
    @DisplayName("Returns null for empty string")
    void testFromProtocolStringWithEmptyString() {
      assertNull(Message.fromProtocolString(""));
    }

    /**
     * Verifies blank string returns null.
     */
    @Test
    @DisplayName("Returns null for blank string")
    void testFromProtocolStringWithBlankString() {
      assertNull(Message.fromProtocolString("   "));
    }

    /**
     * Verifies invalid format without pipe returns null.
     */
    @Test
    @DisplayName("Returns null for invalid format (no pipe)")
    void testFromProtocolStringWithInvalidFormat() {
      assertNull(Message.fromProtocolString("INVALID_FORMAT"));
    }

    /**
     * Verifies single field (no nodeId) returns null.
     */
    @Test
    @DisplayName("Returns null for invalid format (single field)")
    void testFromProtocolStringWithSingleField() {
      assertNull(Message.fromProtocolString("DATA"));
    }

    /**
     * Verifies unknown message type returns null.
     */
    @Test
    @DisplayName("Returns null for unknown message type")
    void testFromProtocolStringWithUnknownType() {
      assertNull(Message.fromProtocolString("UNKNOWN_TYPE|sensor-01|data"));
    }

    /**
     * Verifies empty nodeId returns null.
     */
    @Test
    @DisplayName("Returns null for empty nodeId")
    void testFromProtocolStringWithEmptyNodeId() {
      assertNull(Message.fromProtocolString("DATA||temperature:22.5"));
    }

    /**
     * Verifies blank nodeId (after trimming) returns null.
     */
    @Test
    @DisplayName("Returns null for blank nodeId (after trim)")
    void testFromProtocolStringWithBlankNodeId() {
      assertNull(Message.fromProtocolString("DATA|   |temperature:22.5"));
    }
  }

  /**
   * Tests for round-trip serialization and deserialization.
   */
  @Nested
  @DisplayName("Round-trip Tests")
  class RoundTripTests {

    /**
     * Verifies message with data survives round-trip conversion.
     */
    @Test
    @DisplayName("Preserves message with data")
    void testRoundTripWithData() {
      Message original = new Message(MessageType.COMMAND, "sensor-01", "heater:1");
      String protocolString = original.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(original.getMessageType(), parsed.getMessageType());
      assertEquals(original.getNodeId(), parsed.getNodeId());
      assertEquals(original.getData(), parsed.getData());
    }

    /**
     * Verifies message without data survives round-trip conversion.
     */
    @Test
    @DisplayName("Preserves message without data")
    void testRoundTripWithoutData() {
      Message original = new Message(MessageType.HELLO, "control-01", "");
      String protocolString = original.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(original.getMessageType(), parsed.getMessageType());
      assertEquals(original.getNodeId(), parsed.getNodeId());
      assertEquals(original.getData(), parsed.getData());
    }

    /**
     * Verifies complex data with multiple separators survives round-trip.
     */
    @Test
    @DisplayName("Preserves complex data with multiple separators")
    void testRoundTripWithComplexData() {
      Message original = new Message(MessageType.DATA, "sensor-01",
              "temperature#temp-01:22.5,humidity#humid-01:65.0");
      String protocolString = original.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(original.getData(), parsed.getData());
    }

    /**
     * Verifies data with pipe characters survives round-trip conversion.
     */
    @Test
    @DisplayName("Preserves data with pipe characters")
    void testRoundTripWithPipesInData() {
      Message original = new Message(MessageType.DATA, "sensor-01", "key1:val1|key2:val2|key3:val3");
      String protocolString = original.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(original.getData(), parsed.getData());
    }
  }

  /**
   * Tests for all MessageType enum values.
   */
  @Nested
  @DisplayName("All MessageType Tests")
  class MessageTypeTests {

    /**
     * Verifies all MessageType values can be serialized.
     */
    @Test
    @DisplayName("All MessageType values can be serialized")
    void testAllMessageTypesSerialize() {
      for (MessageType type : MessageType.values()) {
        Message message = new Message(type, "test-node", "test-data");
        String protocolString = message.toProtocolString();

        assertNotNull(protocolString);
        assertTrue(protocolString.startsWith(type.name()),
                "Expected: " + type.name() + ", Got: " + protocolString);
      }
    }

    /**
     * Verifies all MessageType values can be deserialized.
     */
    @Test
    @DisplayName("All MessageType values can be deserialized")
    void testAllMessageTypesDeserialize() {
      for (MessageType type : MessageType.values()) {
        String protocolString = type.name() + "|test-node|test-data";
        Message message = Message.fromProtocolString(protocolString);

        assertNotNull(message, "Failed to parse: " + type);
        assertEquals(type, message.getMessageType());
        assertEquals("test-node", message.getNodeId());
        assertEquals("test-data", message.getData());
      }
    }

    /**
     * Verifies all MessageType values survive round-trip conversion.
     */
    @Test
    @DisplayName("All MessageType values round-trip correctly")
    void testAllMessageTypesRoundTrip() {
      for (MessageType type : MessageType.values()) {
        Message original = new Message(type, "test-node", "test-data");
        String protocolString = original.toProtocolString();
        Message parsed = Message.fromProtocolString(protocolString);

        assertNotNull(parsed, "Round-trip failed for: " + type);
        assertEquals(type, parsed.getMessageType());
        assertEquals("test-node", parsed.getNodeId());
        assertEquals("test-data", parsed.getData());
      }
    }
  }

  /**
   * Tests for specific message types used in the protocol.
   */
  @Nested
  @DisplayName("Specific Message Type Tests")
  class SpecificMessageTypeTests {

    /**
     * Verifies DATA message format with sensor readings.
     */
    @Test
    @DisplayName("DATA message with sensor readings")
    void testDataMessage() {
      Message message = new Message(MessageType.DATA, "sensor-01",
              "temperature#temp-01:22.5,humidity#humid-01:65.0");

      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("DATA|sensor-01|temperature#temp-01:22.5,humidity#humid-01:65.0",
              message.toProtocolString());
    }

    /**
     * Verifies COMMAND message format with actuator command.
     */
    @Test
    @DisplayName("COMMAND message with actuator command")
    void testCommandMessage() {
      Message message = new Message(MessageType.COMMAND, "sensor-01", "heater:1");

      assertEquals(MessageType.COMMAND, message.getMessageType());
      assertEquals("COMMAND|sensor-01|heater:1", message.toProtocolString());
    }

    /**
     * Verifies HELLO message format without data.
     */
    @Test
    @DisplayName("HELLO message without data")
    void testHelloMessage() {
      Message message = new Message(MessageType.HELLO, "sensor-01", "");

      assertEquals("HELLO|sensor-01", message.toProtocolString());
    }

    /**
     * Verifies WELCOME message format without data.
     */
    @Test
    @DisplayName("WELCOME message without data")
    void testWelcomeMessage() {
      Message message = new Message(MessageType.WELCOME, "control-01", "");

      assertEquals("WELCOME|control-01", message.toProtocolString());
    }

    /**
     * Verifies ERROR message format with error description.
     */
    @Test
    @DisplayName("ERROR message with error description")
    void testErrorMessage() {
      Message message = new Message(MessageType.ERROR, "sensor-01", "Unknown actuator");

      assertEquals(MessageType.ERROR, message.getMessageType());
      assertEquals("Unknown actuator", message.getData());
    }

    /**
     * Verifies SUCCESS message format with confirmation data.
     */
    @Test
    @DisplayName("SUCCESS message with confirmation")
    void testSuccessMessage() {
      Message message = new Message(MessageType.SUCCESS, "sensor-01", "heater:1");

      assertEquals(MessageType.SUCCESS, message.getMessageType());
      assertEquals("heater:1", message.getData());
    }

    /**
     * Verifies KEEPALIVE message format without data.
     */
    @Test
    @DisplayName("KEEPALIVE message without data")
    void testKeepaliveMessage() {
      Message message = new Message(MessageType.KEEPALIVE, "sensor-01", "");

      assertEquals("KEEPALIVE|sensor-01", message.toProtocolString());
    }
  }

  /**
   * Tests for edge cases and boundary conditions.
   */
  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    /**
     * Verifies handling of very long nodeId strings.
     */
    @Test
    @DisplayName("Message with very long nodeId")
    void testVeryLongNodeId() {
      String longNodeId = "sensor-" + "x".repeat(100);
      Message message = new Message(MessageType.DATA, longNodeId, "data");

      assertEquals(longNodeId, message.getNodeId());

      String protocolString = message.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);
      assertEquals(longNodeId, parsed.getNodeId());
    }

    /**
     * Verifies handling of very long data strings.
     */
    @Test
    @DisplayName("Message with very long data")
    void testVeryLongData() {
      String longData = "temperature:22.5," + "x".repeat(1000);
      Message message = new Message(MessageType.DATA, "sensor-01", longData);

      assertEquals(longData, message.getData());

      String protocolString = message.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);
      assertEquals(longData, parsed.getData());
    }

    /**
     * Verifies Unicode characters are preserved in round-trip conversion.
     */
    @Test
    @DisplayName("Message with Unicode characters")
    void testUnicodeCharacters() {
      String unicodeData = "temp:22°C,status:✓,location:北京";
      Message message = new Message(MessageType.DATA, "sensor-01", unicodeData);
      String protocolString = message.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(unicodeData, parsed.getData());
    }

    /**
     * Verifies newline characters in data are preserved.
     */
    @Test
    @DisplayName("Message with newline characters in data")
    void testNewlineInData() {
      String dataWithNewline = "line1\nline2\nline3";
      Message message = new Message(MessageType.DATA, "sensor-01", dataWithNewline);
      String protocolString = message.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(dataWithNewline, parsed.getData());
    }

    /**
     * Verifies multiple consecutive pipes in data are preserved.
     */
    @Test
    @DisplayName("Message with multiple consecutive pipes in data")
    void testMultiplePipesInData() {
      String dataWithPipes = "a|||b|||c";
      Message message = new Message(MessageType.DATA, "sensor-01", dataWithPipes);
      String protocolString = message.toProtocolString();
      Message parsed = Message.fromProtocolString(protocolString);

      assertNotNull(parsed);
      assertEquals(dataWithPipes, parsed.getData());
    }
  }
}