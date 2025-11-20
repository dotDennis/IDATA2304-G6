package group6.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Message class
 *
 * @author Fidjor
 * @since 0.2.0
 */
public class MessageTest {

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName ("Create message with all fields populated")
    void testCreateMessageWithAllFields() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "temperature: 22.5");

      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("temperature: 22.5", message.getData());
    }

    @Test
    @DisplayName("Create message with empty data string")
    void testCreateMessageWithEmptyData() {
      Message message = new Message(MessageType.HELLO, "control-01", "");

      assertEquals(MessageType.HELLO, message.getMessageType());
      assertEquals("control-01", message.getNodeId());
      assertEquals("", message.getData());
    }

    @Test
    @DisplayName("Null data is converted to empty string")
    void testNullDataConvertsToEmptyString() {
      Message message = new Message(MessageType.KEEPALIVE, "sensor-01", null);

      assertEquals("", message.getData());
    }

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

    @Test
    @DisplayName("Blank nodeId throws IllegalArgumentException")
    void testBlankNodeIdThrowsException() {
      assertThrows(IllegalArgumentException.class, () -> {
        new Message(MessageType.DATA, "   ", "data");
      });
    }

    @Test
    @DisplayName("Empty nodeId throws IllegalArgumentException")
    void testEmptyNodeIdThrowsException() {
      assertThrows(IllegalArgumentException.class, () -> {
        new Message(MessageType.DATA, "", "data");
      });
    }

    @Test
    @DisplayName("Null messageType throws NullPointerException")
    void testNullMessageTypeThrowsException() {
      assertThrows(NullPointerException.class, () -> {
        new Message(null, "sensor-01", "data");
      });
    }
  }

  @Nested
  @DisplayName("Serialization Tests - toProtocolString()")
  class SerializationTests {

    @Test
    @DisplayName("Formats message with data correctly")
    void testToProtocolStringWithData() {
      Message message = new Message(MessageType.DATA, "sensor-01", "temperature:22.5");

      assertEquals("DATA|sensor-01|temperature:22.5", message.toProtocolString());
    }

    @Test
    @DisplayName("Formats message without data correctly")
    void testToProtocolStringWithoutData() {
      Message message = new Message(MessageType.HELLO, "control-01", "");

      assertEquals("HELLO|control-01", message.toProtocolString());
    }

    @Test
    @DisplayName("Handles null data as empty (no data field)")
    void testToProtocolStringWithNullData() {
      Message message = new Message(MessageType.KEEPALIVE, "sensor-01", null);

      assertEquals("KEEPALIVE|sensor-01", message.toProtocolString());
    }

    @Test
    @DisplayName("Includes data with pipe characters")
    void testToProtocolStringWithPipeInData() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "key1:value1|key2:value2");

      assertEquals("DATA|sensor-01|key1:value1|key2:value2", message.toProtocolString());
    }

    @Test
    @DisplayName("Preserves whitespace in data field")
    void testToProtocolStringPreservesDataWhitespace() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "  data with spaces  ");

      assertEquals("DATA|sensor-01|  data with spaces  ", message.toProtocolString());
    }

    @Test
    @DisplayName("Includes special characters in data")
    void testToProtocolStringWithSpecialCharacters() {
      Message message = new Message(MessageType.DATA,
              "sensor-01", "temp:22.5°C,status:OK✓");

      assertEquals("DATA|sensor-01|temp:22.5°C,status:OK✓", message.toProtocolString());
    }
  }
  @Nested
  @DisplayName("Deserialization Tests - fromProtocolString()")
  class DeserializationTests {

    @Test
    @DisplayName("Parses valid message with data")
    void testFromProtocolStringWithData() {
      Message message = Message.fromProtocolString("DATA|sensor-01|temperature:22.5");

      assertNotNull(message);
      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("temperature:22.5", message.getData());
    }

    @Test
    @DisplayName("Parses valid message without data field")
    void testFromProtocolStringWithoutData() {
      Message message = Message.fromProtocolString("HELLO|control-01");

      assertNotNull(message);
      assertEquals(MessageType.HELLO, message.getMessageType());
      assertEquals("control-01", message.getNodeId());
      assertEquals("", message.getData());
    }

    @Test
    @DisplayName("Parses message with empty data field")
    void testFromProtocolStringWithEmptyDataField() {
      Message message = Message.fromProtocolString("KEEPALIVE|sensor-01|");

      assertNotNull(message);
      assertEquals(MessageType.KEEPALIVE, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("", message.getData());
    }

    @Test
    @DisplayName("Handles data with pipe characters (split limit 3)")
    void testFromProtocolStringDataWithPipes() {
      Message message = Message.fromProtocolString("DATA|sensor-01|key1:value1|key2:value2");

      assertNotNull(message);
      assertEquals("key1:value1|key2:value2", message.getData());
    }

    @Test
    @DisplayName("Preserves whitespace in data field")
    void testFromProtocolStringPreservesDataWhitespace() {
      Message message = Message.fromProtocolString("DATA|sensor-01|  data with spaces  ");

      assertNotNull(message);
      assertEquals("  data with spaces  ", message.getData());
    }

    @Test
    @DisplayName("Trims whitespace from outer protocol string")
    void testFromProtocolStringTrimsOuterWhitespace() {
      Message message = Message.fromProtocolString("  DATA|sensor-01|temperature:22.5");

      assertNotNull(message);
      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("sensor-01", message.getNodeId());
      assertEquals("temperature:22.5", message.getData());
    }

    @Test
    @DisplayName("Trims whitespace from messageType")
    void testFromProtocolStringTrimsMessageType() {
      Message message = Message.fromProtocolString("  DATA  |sensor-01|temperature:22.5");

      assertNotNull(message);
      assertEquals(MessageType.DATA, message.getMessageType());
    }

    @Test
    @DisplayName("Trims whitespace from nodeId")
    void testFromProtocolStringTrimsNodeId() {
      Message message = Message.fromProtocolString("DATA|  sensor-01  |temperature:22.5");

      assertNotNull(message);
      assertEquals("sensor-01", message.getNodeId());
    }

    @Test
    @DisplayName("Returns null for null input")
    void testFromProtocolStringWithNull() {
      assertNull(Message.fromProtocolString(null));
    }

    @Test
    @DisplayName("Returns null for empty string")
    void testFromProtocolStringWithEmptyString() {
      assertNull(Message.fromProtocolString(""));
    }

    @Test
    @DisplayName("Returns null for blank string")
    void testFromProtocolStringWithBlankString() {
      assertNull(Message.fromProtocolString("   "));
    }

    @Test
    @DisplayName("Returns null for invalid format (no pipe)")
    void testFromProtocolStringWithInvalidFormat() {
      assertNull(Message.fromProtocolString("INVALID_FORMAT"));
    }

    @Test
    @DisplayName("Returns null for invalid format (single field)")
    void testFromProtocolStringWithSingleField() {
      assertNull(Message.fromProtocolString("DATA"));
    }

    @Test
    @DisplayName("Returns null for unknown message type")
    void testFromProtocolStringWithUnknownType() {
      assertNull(Message.fromProtocolString("UNKNOWN_TYPE|sensor-01|data"));
    }

    @Test
    @DisplayName("Returns null for empty nodeId")
    void testFromProtocolStringWithEmptyNodeId() {
      assertNull(Message.fromProtocolString("DATA||temperature:22.5"));
    }

    @Test
    @DisplayName("Returns null for blank nodeId (after trim)")
    void testFromProtocolStringWithBlankNodeId() {
      assertNull(Message.fromProtocolString("DATA|   |temperature:22.5"));
    }
  }

  @Nested
  @DisplayName("Round-trip Tests")
  class RoundTripTests {

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

  @Nested
  @DisplayName("All MessageType Tests")
  class MessageTypeTests {

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

  @Nested
  @DisplayName("Specific Message Type Tests")
  class SpecificMessageTypeTests {

    @Test
    @DisplayName("DATA message with sensor readings")
    void testDataMessage() {
      Message message = new Message(MessageType.DATA, "sensor-01",
              "temperature#temp-01:22.5,humidity#humid-01:65.0");

      assertEquals(MessageType.DATA, message.getMessageType());
      assertEquals("DATA|sensor-01|temperature#temp-01:22.5,humidity#humid-01:65.0",
              message.toProtocolString());
    }

    @Test
    @DisplayName("COMMAND message with actuator command")
    void testCommandMessage() {
      Message message = new Message(MessageType.COMMAND, "sensor-01", "heater:1");

      assertEquals(MessageType.COMMAND, message.getMessageType());
      assertEquals("COMMAND|sensor-01|heater:1", message.toProtocolString());
    }

    @Test
    @DisplayName("HELLO message without data")
    void testHelloMessage() {
      Message message = new Message(MessageType.HELLO, "sensor-01", "");

      assertEquals("HELLO|sensor-01", message.toProtocolString());
    }

    @Test
    @DisplayName("WELCOME message without data")
    void testWelcomeMessage() {
      Message message = new Message(MessageType.WELCOME, "control-01", "");

      assertEquals("WELCOME|control-01", message.toProtocolString());
    }

    @Test
    @DisplayName("ERROR message with error description")
    void testErrorMessage() {
      Message message = new Message(MessageType.ERROR, "sensor-01", "Unknown actuator");

      assertEquals(MessageType.ERROR, message.getMessageType());
      assertEquals("Unknown actuator", message.getData());
    }

    @Test
    @DisplayName("SUCCESS message with confirmation")
    void testSuccessMessage() {
      Message message = new Message(MessageType.SUCCESS, "sensor-01", "heater:1");

      assertEquals(MessageType.SUCCESS, message.getMessageType());
      assertEquals("heater:1", message.getData());
    }

    @Test
    @DisplayName("KEEPALIVE message without data")
    void testKeepaliveMessage() {
      Message message = new Message(MessageType.KEEPALIVE, "sensor-01", "");

      assertEquals("KEEPALIVE|sensor-01", message.toProtocolString());
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

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