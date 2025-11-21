package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.sensor.Sensor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential unit tests for Actuator implementations.
 *
 *Tests verify:
 *
 * Actuator creation for all types (Heater, Fan, Window, Valve, Door Lock, Light)
 * State management (ON/OFF toggling)
 * Physical effects on sensors (e.g., heater increases temperature)
 * ActuatorType enum properties
 *
 *Uses a {@link StubSensor} for deterministic verification of actuator influences.
 */
class ActuatorTest {
  /**
   * Simple deterministic sensor used to verify actuator influence.
   *
   *This stub sensor tracks the total external influence applied to it via
   *{@link #addExternalInfluence(double)}, allowing tests to verify that actuators
   * are correctly affecting sensors.
   */
  private static class StubSensor extends Sensor {

    /** Accumulated total of all external influences applied to this sensor. */
    private double influenceTotal = 0.0;

    /**
     * Constructs a stub sensor for testing.
     *
     * @param id the unique sensor identifier
     * @param type the sensor type
     */
    StubSensor(String id, SensorType type) {
      super(id, type, 0, 100);
    }

    /**
     * Returns the current sensor value without simulation.
     *
     * @return the current value
     */
    @Override
    public double readValue() {
      return getCurrentValue();
    }

    /**
     * Applies external influence and tracks the total for testing.
     *
     * @param delta the influence value to apply
     */
    @Override
    public synchronized void addExternalInfluence(double delta) {
      super.addExternalInfluence(delta);
      influenceTotal += delta;
    }

    /**
     * Returns the accumulated total of all external influences.
     *
     * @return the total influence applied to this sensor
     */
    double getInfluenceTotal() {
      return influenceTotal;
    }
  }

  /**
   * Tests for actuator creation and initialization.
   */
  @Nested
  @DisplayName("Actuator Creation")
  class CreationTests {

    /**
     * Verifies that all actuator types can be instantiated and start in OFF state.
     */
    @Test
    @DisplayName("Create all actuator types")
    void testCreateAllActuatorTypes() {
      Actuator[] actuators = {
              new HeaterActuator("heater-01"),
              new FanActuator("fan-01"),
              new WindowOpenerActuator("window-01"),
              new ValveActuator("valve-01"),
              new DoorLockActuator("door-01"),
              new LightSwitchActuator("light-01")
      };

      for (Actuator actuator : actuators) {
        assertNotNull(actuator.getDeviceId());
        assertNotNull(actuator.getDeviceType());
        assertFalse(actuator.getState(), "Actuator should start OFF");
      }
    }

    /**
     * Verifies HeaterActuator properties are correctly initialized.
     */
    @Test
    @DisplayName("HeaterActuator has correct properties")
    void testHeaterActuator() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      assertEquals("heater-01", heater.getDeviceId());
      assertEquals(ActuatorType.HEATER, heater.getDeviceType());
      assertFalse(heater.getState());
    }

    /**
     * Verifies FanActuator properties are correctly initialized.
     */
    @Test
    @DisplayName("FanActuator has correct properties")
    void testFanActuator() {
      FanActuator fan = new FanActuator("fan-01");

      assertEquals("fan-01", fan.getDeviceId());
      assertEquals(ActuatorType.FAN, fan.getDeviceType());
      assertFalse(fan.getState());
    }
  }

  /**
   * Tests for actuator state management (ON/OFF).
   */
  @Nested
  @DisplayName("State Management")
  class StateTests {

    /**
     * Verifies actuators start in OFF state by default.
     */
    @Test
    @DisplayName("Actuator starts in OFF state")
    void testInitialStateIsOff() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      assertFalse(heater.getState());
    }

    /**
     * Verifies setState(true) turns the actuator ON.
     */
    @Test
    @DisplayName("setState() turns actuator ON")
    void testSetStateOn() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      heater.setState(true);

      assertTrue(heater.getState());
    }

    /**
     * Verifies setState(false) turns the actuator OFF.
     */
    @Test
    @DisplayName("setState() turns actuator OFF")
    void testSetStateOff() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      heater.setState(true);

      heater.setState(false);

      assertFalse(heater.getState());
    }

    /**
     * Verifies setting the same state value does not change state.
     */
    @Test
    @DisplayName("setState() with same value does not change state")
    void testSetStateSameValue() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      heater.setState(false);
      assertFalse(heater.getState());

      heater.setState(false);
      assertFalse(heater.getState());
    }

    /**
     * Verifies multiple state changes work correctly.
     */
    @Test
    @DisplayName("Multiple setState() calls work correctly")
    void testMultipleSetStateCalls() {
      FanActuator fan = new FanActuator("fan-01");

      fan.setState(true);
      assertTrue(fan.getState());

      fan.setState(false);
      assertFalse(fan.getState());

      fan.setState(true);
      assertTrue(fan.getState());
    }
  }

  /**
   * Tests for actuator physical effects on sensors.
   */
  @Nested
  @DisplayName("Actuator Effects")
  class EffectTests {

    /**
     * Verifies HeaterActuator applies positive influence to temperature sensors.
     */
    @Test
    @DisplayName("Heater applies positive influence to temperature")
    void testHeaterIncreasesTemperature() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      StubSensor tempSensor = new StubSensor("temp-01", SensorType.TEMPERATURE);

      heater.applyEffect(List.of(tempSensor));

      assertTrue(tempSensor.getInfluenceTotal() > 0.0, "Heater should warm the air");
    }

    /**
     * Verifies HeaterActuator applies negative influence to humidity sensors.
     */
    @Test
    @DisplayName("Heater applies negative influence to humidity")
    void testHeaterDecreasesHumidity() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      StubSensor humiditySensor = new StubSensor("humid-01", SensorType.HUMIDITY);

      heater.applyEffect(List.of(humiditySensor));

      assertTrue(humiditySensor.getInfluenceTotal() < 0.0, "Heater should dry the air");
    }

    /**
     * Verifies FanActuator applies negative influence to temperature sensors.
     */
    @Test
    @DisplayName("Fan cools air via negative temperature influence")
    void testFanDecreasesTemperature() {
      FanActuator fan = new FanActuator("fan-01");
      StubSensor tempSensor = new StubSensor("temp-01", SensorType.TEMPERATURE);

      fan.applyEffect(List.of(tempSensor));

      assertTrue(tempSensor.getInfluenceTotal() < 0.0, "Fan should cool the air");
    }

    /**
     * Verifies FanActuator applies negative influence to humidity sensors.
     */
    @Test
    @DisplayName("Fan reduces humidity through negative influence")
    void testFanDecreasesHumidity() {
      FanActuator fan = new FanActuator("fan-01");
      StubSensor humiditySensor = new StubSensor("humid-01", SensorType.HUMIDITY);

      fan.applyEffect(List.of(humiditySensor));

      assertTrue(humiditySensor.getInfluenceTotal() < 0.0, "Fan should lower humidity");
    }

    /**
     * Verifies FanActuator applies positive influence to wind speed sensors.
     */
    @Test
    @DisplayName("Fan increases wind influence on wind sensors")
    void testFanIncreasesWindSpeed() {
      FanActuator fan = new FanActuator("fan-01");
      StubSensor windSensor = new StubSensor("wind-01", SensorType.WIND_SPEED);

      fan.applyEffect(List.of(windSensor));

      assertTrue(windSensor.getInfluenceTotal() > 0.0, "Fan should increase wind speed");
    }

    /**
     * Verifies applyEffect() handles empty sensor lists without error.
     */
    @Test
    @DisplayName("applyEffect() works with empty sensor list")
    void testApplyEffectWithEmptySensorList() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      List<Sensor> emptySensors = new ArrayList<>();

      assertDoesNotThrow(() -> heater.applyEffect(emptySensors));
    }

    /**
     * Verifies applyEffect() correctly influences multiple sensors simultaneously.
     */
    @Test
    @DisplayName("applyEffect() affects multiple sensors")
    void testApplyEffectWithMultipleSensors() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      StubSensor tempSensor = new StubSensor("temp-01", SensorType.TEMPERATURE);
      StubSensor humiditySensor = new StubSensor("humid-01", SensorType.HUMIDITY);

      heater.applyEffect(List.of(tempSensor, humiditySensor));

      assertTrue(tempSensor.getInfluenceTotal() > 0.0);
      assertTrue(humiditySensor.getInfluenceTotal() < 0.0);
    }
  }

  /**
   * Tests for ActuatorType enum properties.
   */
  @Nested
  @DisplayName("ActuatorType Tests")
  class ActuatorTypeTests {

    /**
     * Verifies all ActuatorType values have non-blank display names.
     */
    @Test
    @DisplayName("All ActuatorTypes have display names")
    void testActuatorTypeDisplayNames() {
      for (ActuatorType type : ActuatorType.values()) {
        assertNotNull(type.getDisplayName());
        assertFalse(type.getDisplayName().isBlank());
      }
    }

    /**
     * Verifies ActuatorType display names are human-readable.
     */
    @Test
    @DisplayName("ActuatorType display names are readable")
    void testActuatorTypeDisplayNamesReadable() {
      assertEquals("Heater", ActuatorType.HEATER.getDisplayName());
      assertEquals("Fan", ActuatorType.FAN.getDisplayName());
      assertEquals("Window Opener", ActuatorType.WINDOW_OPENER.getDisplayName());
      assertEquals("Door Lock", ActuatorType.DOOR_LOCK.getDisplayName());
      assertEquals("Light Switch", ActuatorType.LIGHT_SWITCH.getDisplayName());
      assertEquals("Valve", ActuatorType.VALVE.getDisplayName());
    }
  }
}
