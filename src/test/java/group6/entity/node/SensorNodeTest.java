package group6.entity.node;

import group6.entity.device.actuator.Actuator;
import group6.entity.device.actuator.FanActuator;
import group6.entity.device.actuator.HeaterActuator;
import group6.entity.device.sensor.HumiditySensor;
import group6.entity.device.sensor.TemperatureSensor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Essential unit tests for SensorNode.
 */
class SensorNodeTest {

  @Nested
  @DisplayName("SensorNode Creation")
  class CreationTests {

    @Test
    @DisplayName("Create SensorNode with valid ID")
    void testCreateSensorNode() {
      SensorNode node = new SensorNode("node-01");

      assertEquals("node-01", node.getNodeId());
      assertEquals(NodeType.SENSOR, node.getNodeType());
      assertTrue(node.getSensors().isEmpty());
      assertTrue(node.getActuators().isEmpty());
    }

    @Test
    @DisplayName("Default interval is 5000ms")
    void testDefaultInterval() {
      SensorNode node = new SensorNode("node-01");

      assertEquals(5000L, node.getSensorNodeInterval());
    }
  }

  @Nested
  @DisplayName("Device Management - Add")
  class AddDeviceTests {

    private SensorNode node;

    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
    }

    @Test
    @DisplayName("addSensor() adds sensor to node")
    void testAddSensor() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      node.addSensor(sensor);

      assertEquals(1, node.getSensors().size());
      assertTrue(node.getSensors().contains(sensor));
    }

    @Test
    @DisplayName("addActuator() adds actuator to node")
    void testAddActuator() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      node.addActuator(heater);

      assertEquals(1, node.getActuators().size());
      assertTrue(node.getActuators().contains(heater));
    }

    @Test
    @DisplayName("addSensor() throws exception for null sensor")
    void testAddNullSensor() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.addSensor(null);
      });
    }

    @Test
    @DisplayName("addActuator() throws exception for null actuator")
    void testAddNullActuator() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.addActuator(null);
      });
    }

    @Test
    @DisplayName("Add multiple sensors")
    void testAddMultipleSensors() {
      TemperatureSensor temp = new TemperatureSensor("temp-01");
      HumiditySensor humid = new HumiditySensor("humid-01");

      node.addSensor(temp);
      node.addSensor(humid);

      assertEquals(2, node.getSensors().size());
    }

    @Test
    @DisplayName("Add multiple actuators")
    void testAddMultipleActuators() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      FanActuator fan = new FanActuator("fan-01");

      node.addActuator(heater);
      node.addActuator(fan);

      assertEquals(2, node.getActuators().size());
    }
  }

  @Nested
  @DisplayName("Device Management - Remove")
  class RemoveDeviceTests {

    private SensorNode node;
    private TemperatureSensor sensor;
    private HeaterActuator actuator;

    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
      sensor = new TemperatureSensor("temp-01");
      actuator = new HeaterActuator("heater-01");
      node.addSensor(sensor);
      node.addActuator(actuator);
    }

    @Test
    @DisplayName("removeSensor() removes sensor from node")
    void testRemoveSensor() {
      boolean removed = node.removeSensor(sensor);

      assertTrue(removed);
      assertEquals(0, node.getSensors().size());
    }

    @Test
    @DisplayName("removeActuator() removes actuator from node")
    void testRemoveActuator() {
      boolean removed = node.removeActuator(actuator);

      assertTrue(removed);
      assertEquals(0, node.getActuators().size());
    }

    @Test
    @DisplayName("removeSensor() returns false for non-existent sensor")
    void testRemoveNonExistentSensor() {
      TemperatureSensor other = new TemperatureSensor("temp-02");

      boolean removed = node.removeSensor(other);

      assertFalse(removed);
      assertEquals(1, node.getSensors().size());
    }

    @Test
    @DisplayName("removeActuator() returns false for non-existent actuator")
    void testRemoveNonExistentActuator() {
      HeaterActuator other = new HeaterActuator("heater-02");

      boolean removed = node.removeActuator(other);

      assertFalse(removed);
      assertEquals(1, node.getActuators().size());
    }

    @Test
    @DisplayName("removeSensor() throws exception for null")
    void testRemoveNullSensor() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.removeSensor(null);
      });
    }

    @Test
    @DisplayName("removeActuator() throws exception for null")
    void testRemoveNullActuator() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.removeActuator(null);
      });
    }
  }

  @Nested
  @DisplayName("Protocol String Building")
  class ProtocolStringTests {

    private SensorNode node;

    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
    }

    @Test
    @DisplayName("getSensorSnapshot() with no sensors returns empty string")
    void testGetSensorSnapshotEmpty() {
      String snapshot = node.getSensorSnapshot();

      assertEquals("", snapshot);
    }

    @Test
    @DisplayName("getSensorSnapshot() with one sensor")
    void testGetSensorSnapshotOneSensor() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      node.addSensor(sensor);

      String snapshot = node.getSensorSnapshot();

      assertTrue(snapshot.startsWith("temperature#temp-01:"));
      assertTrue(snapshot.contains(":"));
    }

    @Test
    @DisplayName("getSensorSnapshot() with multiple sensors")
    void testGetSensorSnapshotMultipleSensors() {
      TemperatureSensor temp = new TemperatureSensor("temp-01");
      HumiditySensor humid = new HumiditySensor("humid-01");
      node.addSensor(temp);
      node.addSensor(humid);

      String snapshot = node.getSensorSnapshot();

      assertTrue(snapshot.contains("temperature#temp-01:"));
      assertTrue(snapshot.contains("humidity#humid-01:"));
      assertTrue(snapshot.contains(","));
    }

    @Test
    @DisplayName("getActuatorSnapshot() with no actuators returns empty string")
    void testGetActuatorSnapshotEmpty() {
      String snapshot = node.getActuatorSnapshot();

      assertEquals("", snapshot);
    }

    @Test
    @DisplayName("getActuatorSnapshot() with one actuator OFF")
    void testGetActuatorSnapshotOneActuatorOff() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      node.addActuator(heater);

      String snapshot = node.getActuatorSnapshot();

      assertEquals("heater#heater-01:0", snapshot);
    }

    @Test
    @DisplayName("getActuatorSnapshot() with one actuator ON")
    void testGetActuatorSnapshotOneActuatorOn() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      heater.setState(true);
      node.addActuator(heater);

      String snapshot = node.getActuatorSnapshot();

      assertEquals("heater#heater-01:1", snapshot);
    }

    @Test
    @DisplayName("getActuatorSnapshot() with multiple actuators")
    void testGetActuatorSnapshotMultipleActuators() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      FanActuator fan = new FanActuator("fan-01");
      heater.setState(true);
      fan.setState(false);
      node.addActuator(heater);
      node.addActuator(fan);

      String snapshot = node.getActuatorSnapshot();

      assertTrue(snapshot.contains("heater#heater-01:1"));
      assertTrue(snapshot.contains("fan#fan-01:0"));
      assertTrue(snapshot.contains(","));
    }
  }

  @Nested
  @DisplayName("Search Methods")
  class SearchTests {

    private SensorNode node;
    private HeaterActuator heater;
    private FanActuator fan;

    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
      heater = new HeaterActuator("heater-01");
      fan = new FanActuator("fan-01");
      node.addActuator(heater);
      node.addActuator(fan);
    }

    @Test
    @DisplayName("findActuatorByType() finds actuator case-insensitive")
    void testFindActuatorByType() {
      Actuator found = node.findActuatorByType("HEATER");

      assertNotNull(found);
      assertEquals(heater, found);
    }

    @Test
    @DisplayName("findActuatorByType() with lowercase")
    void testFindActuatorByTypeLowercase() {
      Actuator found = node.findActuatorByType("heater");

      assertNotNull(found);
      assertEquals(heater, found);
    }

    @Test
    @DisplayName("findActuatorByType() returns null for non-existent type")
    void testFindActuatorByTypeNotFound() {
      Actuator found = node.findActuatorByType("WINDOW");

      assertNull(found);
    }

    @Test
    @DisplayName("findActuatorByType() returns null for null input")
    void testFindActuatorByTypeNull() {
      Actuator found = node.findActuatorByType(null);

      assertNull(found);
    }

    @Test
    @DisplayName("findActuatorByDeviceId() finds actuator case-insensitive")
    void testFindActuatorByDeviceId() {
      Actuator found = node.findActuatorByDeviceId("HEATER-01");

      assertNotNull(found);
      assertEquals(heater, found);
    }

    @Test
    @DisplayName("findActuatorByDeviceId() with lowercase")
    void testFindActuatorByDeviceIdLowercase() {
      Actuator found = node.findActuatorByDeviceId("heater-01");

      assertNotNull(found);
      assertEquals(heater, found);
    }

    @Test
    @DisplayName("findActuatorByDeviceId() returns null for non-existent id")
    void testFindActuatorByDeviceIdNotFound() {
      Actuator found = node.findActuatorByDeviceId("heater-99");

      assertNull(found);
    }

    @Test
    @DisplayName("findActuatorByDeviceId() returns null for null input")
    void testFindActuatorByDeviceIdNull() {
      Actuator found = node.findActuatorByDeviceId(null);

      assertNull(found);
    }
  }

  @Nested
  @DisplayName("Interval Management")
  class IntervalTests {

    private SensorNode node;

    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
    }

    @Test
    @DisplayName("setSensorNodeInterval() sets valid interval")
    void testSetValidInterval() {
      node.setSensorNodeInterval(10000L);

      assertEquals(10000L, node.getSensorNodeInterval());
    }

    @Test
    @DisplayName("setSensorNodeInterval() throws exception for zero")
    void testSetIntervalZero() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.setSensorNodeInterval(0L);
      });
    }

    @Test
    @DisplayName("setSensorNodeInterval() throws exception for negative")
    void testSetIntervalNegative() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.setSensorNodeInterval(-1000L);
      });
    }
  }

  @Nested
  @DisplayName("Actuator Effects Integration")
  class ActuatorEffectTests {

    @Test
    @DisplayName("getSensorSnapshot() applies actuator effects when ON")
    void testSensorSnapshotAppliesEffects() {
      SensorNode node = new SensorNode("node-01");
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      HeaterActuator heater = new HeaterActuator("heater-01");

      node.addSensor(sensor);
      node.addActuator(heater);
      heater.setState(true);

      double initialTemp = sensor.getCurrentValue();
      node.getSensorSnapshot();
      double finalTemp = sensor.getCurrentValue();

      assertTrue(finalTemp > initialTemp,
              "Heater should increase temperature during snapshot");
    }

    @Test
    @DisplayName("getSensorSnapshot() does not apply effects when actuator OFF")
    void testSensorSnapshotNoEffectsWhenOff() {
      SensorNode node = new SensorNode("node-01");
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      HeaterActuator heater = new HeaterActuator("heater-01");

      node.addSensor(sensor);
      node.addActuator(heater);
      heater.setState(false);

      double initialTemp = sensor.getCurrentValue();
      node.getSensorSnapshot();
      double finalTemp = sensor.getCurrentValue();

      assertEquals(initialTemp, finalTemp, 0.001,
              "Temperature should not change when heater is OFF");
    }
  }
}