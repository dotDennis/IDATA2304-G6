package group6.entity.device.actuator;

import group6.entity.device.ActuatorType;
import group6.entity.device.sensor.HumiditySensor;
import group6.entity.device.sensor.Sensor;
import group6.entity.device.sensor.TemperatureSensor;
import group6.entity.device.sensor.WindSensor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential unit tests for Actuator implementations.
 */
class ActuatorTest {

  @Nested
  @DisplayName("Actuator Creation")
  class CreationTests {

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

    @Test
    @DisplayName("HeaterActuator has correct properties")
    void testHeaterActuator() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      assertEquals("heater-01", heater.getDeviceId());
      assertEquals(ActuatorType.HEATER, heater.getDeviceType());
      assertFalse(heater.getState());
    }

    @Test
    @DisplayName("FanActuator has correct properties")
    void testFanActuator() {
      FanActuator fan = new FanActuator("fan-01");

      assertEquals("fan-01", fan.getDeviceId());
      assertEquals(ActuatorType.FAN, fan.getDeviceType());
      assertFalse(fan.getState());
    }
  }

  @Nested
  @DisplayName("State Management")
  class StateTests {

    @Test
    @DisplayName("Actuator starts in OFF state")
    void testInitialStateIsOff() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      assertFalse(heater.getState());
    }

    @Test
    @DisplayName("setState() turns actuator ON")
    void testSetStateOn() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      heater.setState(true);

      assertTrue(heater.getState());
    }

    @Test
    @DisplayName("setState() turns actuator OFF")
    void testSetStateOff() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      heater.setState(true);

      heater.setState(false);

      assertFalse(heater.getState());
    }

    @Test
    @DisplayName("setState() with same value does not change state")
    void testSetStateSameValue() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      heater.setState(false);
      assertFalse(heater.getState());

      heater.setState(false);
      assertFalse(heater.getState());
    }

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

  @Nested
  @DisplayName("Actuator Effects")
  class EffectTests {

    @Test
    @DisplayName("HeaterActuator increases temperature")
    void testHeaterIncreasesTemperature() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      TemperatureSensor tempSensor = new TemperatureSensor("temp-01");
      List<Sensor> sensors = List.of(tempSensor);

      double initialTemp = tempSensor.getCurrentValue();
      heater.applyEffect(sensors);
      double newTemp = tempSensor.getCurrentValue();

      assertTrue(newTemp > initialTemp, "Heater should increase temperature");
    }

    @Test
    @DisplayName("HeaterActuator decreases humidity")
    void testHeaterDecreasesHumidity() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      HumiditySensor humiditySensor = new HumiditySensor("humid-01");
      List<Sensor> sensors = List.of(humiditySensor);

      double initialHumidity = humiditySensor.getCurrentValue();
      heater.applyEffect(sensors);
      double newHumidity = humiditySensor.getCurrentValue();

      assertTrue(newHumidity < initialHumidity, "Heater should decrease humidity");
    }

    @Test
    @DisplayName("FanActuator decreases temperature")
    void testFanDecreasesTemperature() {
      FanActuator fan = new FanActuator("fan-01");
      TemperatureSensor tempSensor = new TemperatureSensor("temp-01");
      List<Sensor> sensors = List.of(tempSensor);

      double initialTemp = tempSensor.getCurrentValue();
      fan.applyEffect(sensors);
      double newTemp = tempSensor.getCurrentValue();

      assertTrue(newTemp < initialTemp, "Fan should decrease temperature");
    }

    @Test
    @DisplayName("FanActuator decreases humidity")
    void testFanDecreasesHumidity() {
      FanActuator fan = new FanActuator("fan-01");
      HumiditySensor humiditySensor = new HumiditySensor("humid-01");
      List<Sensor> sensors = List.of(humiditySensor);

      double initialHumidity = humiditySensor.getCurrentValue();
      fan.applyEffect(sensors);
      double newHumidity = humiditySensor.getCurrentValue();

      assertTrue(newHumidity < initialHumidity, "Fan should decrease humidity");
    }

    @Test
    @DisplayName("FanActuator increases wind speed")
    void testFanIncreasesWindSpeed() {
      FanActuator fan = new FanActuator("fan-01");
      WindSensor windSensor = new WindSensor("wind-01");
      List<Sensor> sensors = List.of(windSensor);

      double initialWind = windSensor.getCurrentValue();
      fan.applyEffect(sensors);
      double newWind = windSensor.getCurrentValue();

      assertTrue(newWind > initialWind, "Fan should increase wind speed");
    }

    @Test
    @DisplayName("applyEffect() works with empty sensor list")
    void testApplyEffectWithEmptySensorList() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      List<Sensor> emptySensors = new ArrayList<>();

      assertDoesNotThrow(() -> heater.applyEffect(emptySensors));
    }

    @Test
    @DisplayName("applyEffect() affects multiple sensors")
    void testApplyEffectWithMultipleSensors() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      TemperatureSensor tempSensor = new TemperatureSensor("temp-01");
      HumiditySensor humiditySensor = new HumiditySensor("humid-01");
      List<Sensor> sensors = List.of(tempSensor, humiditySensor);

      double initialTemp = tempSensor.getCurrentValue();
      double initialHumidity = humiditySensor.getCurrentValue();

      heater.applyEffect(sensors);

      assertTrue(tempSensor.getCurrentValue() > initialTemp);
      assertTrue(humiditySensor.getCurrentValue() < initialHumidity);
    }
  }

  @Nested
  @DisplayName("ActuatorType Tests")
  class ActuatorTypeTests {

    @Test
    @DisplayName("All ActuatorTypes have display names")
    void testActuatorTypeDisplayNames() {
      for (ActuatorType type : ActuatorType.values()) {
        assertNotNull(type.getDisplayName());
        assertFalse(type.getDisplayName().isBlank());
      }
    }

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