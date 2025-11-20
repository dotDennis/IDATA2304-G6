package group6.logic.factory;

import group6.entity.device.ActuatorType;
import group6.entity.device.actuator.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ActuatorFactory.
 */
class ActuatorFactoryTest {

  @Nested
  @DisplayName("Actuator Creation - All Types")
  class ActuatorCreationTests {

    @Test
    @DisplayName("Create FanActuator")
    void testCreateFanActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.FAN, "fan-01");

      assertNotNull(actuator);
      assertInstanceOf(FanActuator.class, actuator);
      assertEquals("fan-01", actuator.getDeviceId());
      assertEquals(ActuatorType.FAN, actuator.getDeviceType());
    }

    @Test
    @DisplayName("Create HeaterActuator")
    void testCreateHeaterActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.HEATER, "heater-01");

      assertNotNull(actuator);
      assertInstanceOf(HeaterActuator.class, actuator);
      assertEquals("heater-01", actuator.getDeviceId());
      assertEquals(ActuatorType.HEATER, actuator.getDeviceType());
    }

    @Test
    @DisplayName("Create WindowOpenerActuator")
    void testCreateWindowOpenerActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.WINDOW_OPENER, "window-01");

      assertNotNull(actuator);
      assertInstanceOf(WindowOpenerActuator.class, actuator);
      assertEquals("window-01", actuator.getDeviceId());
      assertEquals(ActuatorType.WINDOW_OPENER, actuator.getDeviceType());
    }

    @Test
    @DisplayName("Create ValveActuator")
    void testCreateValveActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.VALVE, "valve-01");

      assertNotNull(actuator);
      assertInstanceOf(ValveActuator.class, actuator);
      assertEquals("valve-01", actuator.getDeviceId());
      assertEquals(ActuatorType.VALVE, actuator.getDeviceType());
    }

    @Test
    @DisplayName("Create DoorLockActuator")
    void testCreateDoorLockActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.DOOR_LOCK, "door-01");

      assertNotNull(actuator);
      assertInstanceOf(DoorLockActuator.class, actuator);
      assertEquals("door-01", actuator.getDeviceId());
      assertEquals(ActuatorType.DOOR_LOCK, actuator.getDeviceType());
    }

    @Test
    @DisplayName("Create LightSwitchActuator")
    void testCreateLightSwitchActuator() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.LIGHT_SWITCH, "light-01");

      assertNotNull(actuator);
      assertInstanceOf(LightSwitchActuator.class, actuator);
      assertEquals("light-01", actuator.getDeviceId());
      assertEquals(ActuatorType.LIGHT_SWITCH, actuator.getDeviceType());
    }

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

  @Nested
  @DisplayName("Device ID Validation")
  class DeviceIdValidationTests {

    @Test
    @DisplayName("createActuator() trims whitespace from device ID")
    void testCreateActuatorTrimsDeviceId() {
      Actuator actuator = ActuatorFactory.createActuator(ActuatorType.FAN, "  fan-01  ");

      assertEquals("fan-01", actuator.getDeviceId());
    }

    @Test
    @DisplayName("createActuator() throws exception for null device ID")
    void testCreateActuatorNullDeviceId() {
      Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(ActuatorType.FAN, null);
      });

      assertTrue(exception.getMessage().contains("blank") ||
              exception.getMessage().contains("Device id"));
    }

    @Test
    @DisplayName("createActuator() throws exception for empty device ID")
    void testCreateActuatorEmptyDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(ActuatorType.FAN, "");
      });
    }

    @Test
    @DisplayName("createActuator() throws exception for blank device ID")
    void testCreateActuatorBlankDeviceId() {
      assertThrows(IllegalArgumentException.class, () -> {
        ActuatorFactory.createActuator(ActuatorType.FAN, "   ");
      });
    }
  }

  @Nested
  @DisplayName("Actuator Type Validation")
  class ActuatorTypeValidationTests {

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

  @Nested
  @DisplayName("Created Actuator Properties")
  class ActuatorPropertiesTests {

    @Test
    @DisplayName("Created actuators start in OFF state")
    void testCreatedActuatorsStartOff() {
      for (ActuatorType type : ActuatorType.values()) {
        Actuator actuator = ActuatorFactory.createActuator(type, "test-01");

        assertFalse(actuator.getState(),
                type + " should start in OFF state");
      }
    }

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