package group6.logic.factory;

import group6.entity.device.ActuatorType;
import group6.entity.device.actuator.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ActuatorFactory.
 *
 * Tests verify:
 * Actuator creation for all types (Heater, Fan, Window, Valve, Door Lock, Light)
 * Device ID validation (trimming, null/empty/blank checks)
 * Actuator type validation
 * Properties of created actuators (initial state, functionality)
 */
class ActuatorFactoryTest {

  /**
   * Tests for creating actuators of all supported types.
   */
  @Nested
  @DisplayName("Actuator Creation - All Types")
  class ActuatorCreationTests {

    /**
     * Verifies FanActuator can be created with correct properties.
     */
    @Test
    @DisplayName("Create FanActuator")
    void testCreateFanActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.FAN, "fan-01");

      assertNotNull(actuator);
      assertInstanceOf(FanActuator.class, actuator);
      assertEquals("fan-01", actuator.getDeviceId());
      assertEquals(ActuatorType.FAN, actuator.getDeviceType());
    }

    /**
     * Verifies HeaterActuator can be created with correct properties.
     */
    @Test
    @DisplayName("Create HeaterActuator")
    void testCreateHeaterActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.HEATER, "heater-01");

      assertNotNull(actuator);
      assertInstanceOf(HeaterActuator.class, actuator);
      assertEquals("heater-01", actuator.getDeviceId());
      assertEquals(ActuatorType.HEATER, actuator.getDeviceType());
    }

    /**
     * Verifies WindowOpenerActuator can be created with correct properties.
     */
    @Test
    @DisplayName("Create WindowOpenerActuator")
    void testCreateWindowOpenerActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.WINDOW_OPENER, "window-01");

      assertNotNull(actuator);
      assertInstanceOf(WindowOpenerActuator.class, actuator);
      assertEquals("window-01", actuator.getDeviceId());
      assertEquals(ActuatorType.WINDOW_OPENER, actuator.getDeviceType());
    }

    /**
     * Verifies ValveActuator can be created with correct properties.
     */
    @Test
    @DisplayName("Create ValveActuator")
    void testCreateValveActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.VALVE, "valve-01");

      assertNotNull(actuator);
      assertInstanceOf(ValveActuator.class, actuator);
      assertEquals("valve-01", actuator.getDeviceId());
      assertEquals(ActuatorType.VALVE, actuator.getDeviceType());
    }

    /**
     * Verifies DoorLockActuator can be created with correct properties.
     */
    @Test
    @DisplayName("Create DoorLockActuator")
    void testCreateDoorLockActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.DOOR_LOCK, "door-01");

      assertNotNull(actuator);
      assertInstanceOf(DoorLockActuator.class, actuator);
      assertEquals("door-01", actuator.getDeviceId());
      assertEquals(ActuatorType.DOOR_LOCK, actuator.getDeviceType());
    }

    /**
     * Verifies LightSwitchActuator can be created with correct properties.
     */
    @Test
    @DisplayName("Create LightSwitchActuator")
    void testCreateLightSwitchActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.LIGHT_SWITCH, "light-01");

      assertNotNull(actuator);
      assertInstanceOf(LightSwitchActuator.class, actuator);
      assertEquals("light-01", actuator.getDeviceId());
      assertEquals(ActuatorType.LIGHT_SWITCH, actuator.getDeviceType());
    }

    /**
     * Verifies all ActuatorType enum values can be successfully created.
     * Iterates through all enum values to ensure factory completeness.
     */
    @Test
    @DisplayName("All ActuatorTypes can be created")
    void testAllActuatorTypesCanBeCreated() {
      for (ActuatorType type : ActuatorType.values()) {
        Actuator actuator = ActuatorFactory.createActuator(type, "test-id");

        assertNotNull(actuator, "Failed to create actuator for type: " + type);
        assertEquals(type, actuator.getDeviceType());
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
     * Verifies createActuator() trims leading and trailing whitespace from device IDs.
     */
    @Test
    @DisplayName("createActuator() trims whitespace from device ID")
    void testCreateActuatorTrimsDeviceId() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.FAN, "  fan-01  ");

      assertEquals("fan-01", actuator.getDeviceId());
    }

    /**
     * Verifies createActuator() throws IllegalArgumentException for null device ID.
     */
    @Test
    @DisplayName("createActuator() throws exception for null device ID")
    void testCreateActuatorNullDeviceId() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(ActuatorType.FAN, null);
      });

      assertTrue(exception.getMessage().contains("blank") ||
              exception.getMessage().contains("Device id"));
    }

    /**
     * Verifies createActuator() throws IllegalArgumentException for empty device ID.
     */
    @Test
    @DisplayName("createActuator() throws exception for empty device ID")
    void testCreateActuatorEmptyDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(ActuatorType.FAN, "");
      });
    }

    /**
     * Verifies createActuator() throws IllegalArgumentException for blank device ID.
     * Blank is defined as a string containing only whitespace characters.
     */
    @Test
    @DisplayName("createActuator() throws exception for blank device ID")
    void testCreateActuatorBlankDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(ActuatorType.FAN, "   ");
      });
    }
  }

  /**
   * Tests for actuator type validation.
   */
  @Nested
  @DisplayName("Actuator Type Validation")
  class ActuatorTypeValidationTests {

    /**
     * Verifies createActuator() throws IllegalArgumentException for null actuator type.
     */
    @Test
    @DisplayName("createActuator() throws exception for null actuator type")
    void testCreateActuatorNullType() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(null, "fan-01");
      });

      assertTrue(exception.getMessage().contains("null") ||
              exception.getMessage().contains("type"));
    }
  }

  /**
   * Tests for properties and behavior of created actuators.
   */
  @Nested
  @DisplayName("Created Actuator Properties")
  class ActuatorPropertiesTests {

    /**
     * Verifies all created actuators start in OFF state by default.
     * This is a safety feature ensuring actuators don't activate unexpectedly.
     */
    @Test
    @DisplayName("Created actuators start in OFF state")
    void testCreatedActuatorsStartOff() {
      for (ActuatorType type : ActuatorType.values()) {
        Actuator actuator = ActuatorFactory.createActuator(type, "test-01");

        assertFalse(actuator.getState(),
                type + " should start in OFF state");
      }
    }

    /**
     * Verifies created actuators are fully functional and can change state.
     * Tests the state toggle functionality to ensure factory produces working instances.
     */
    @Test
    @DisplayName("Created actuators are fully functional")
    void testCreatedActuatorsAreFunctional() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.FAN, "fan-01");

      assertFalse(actuator.getState());

      actuator.setState(true);
      assertTrue(actuator.getState());

      actuator.setState(false);
      assertFalse(actuator.getState());
    }

    /**
     * Verifies multiple actuators of the same type can be created with different IDs.
     * Ensures factory creates new instances rather than returning singletons.
     */
    @Test
    @DisplayName("Multiple actuators can be created with same type")
    void testMultipleActuatorsWithSameType() {
      Actuator actuator1 = ActuatorFactory.createActuator(ActuatorType.FAN, "fan-01");
      Actuator actuator2 = ActuatorFactory.createActuator(ActuatorType.FAN, "fan-02");

      assertNotSame(actuator1, actuator2);
      assertEquals("fan-01", actuator1.getDeviceId());
      assertEquals("fan-02", actuator2.getDeviceId());
    }
  }
}