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
 *
 *Tests verify:
 *SensorNode creation and initialization
 *Device management (adding and removing sensors, actuators)
 *Protocol string generation for sensors and actuators
 *Search methods for finding actuators by type or device ID
 *Update interval configuration
 *Integration of actuator effects on sensors
 */
class SensorNodeTest {

  /**
   * Tests for SensorNode creation and initialization.
   */
  @Nested
  @DisplayName("SensorNode Creation")
  class CreationTests {

    /**
     * Verifies SensorNode can be created with a valid ID.
     *
     *Checks that:
     * Node ID is correctly set
     * Node type is SENSOR
     * Initial sensor and actuator lists are empty
     */
    @Test
    @DisplayName("Create SensorNode with valid ID")
    void testCreateSensorNode() {
      SensorNode node = new SensorNode("node-01");

      assertEquals("node-01", node.getNodeId());
      assertEquals(NodeType.SENSOR, node.getNodeType());
      assertTrue(node.getSensors().isEmpty());
      assertTrue(node.getActuators().isEmpty());
    }

    /**
     * Verifies the default update interval is 5000 milliseconds.
     */
    @Test
    @DisplayName("Default interval is 5000ms")
    void testDefaultInterval() {
      SensorNode node = new SensorNode("node-01");

      assertEquals(5000L, node.getSensorNodeInterval());
    }
  }

  /**
   * Tests for adding devices to the SensorNode.
   */
  @Nested
  @DisplayName("Device Management - Add")
  class AddDeviceTests {

    private SensorNode node;

    /**
     * Initializes a fresh SensorNode before each test.
     */
    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
    }

    /**
     * Verifies addSensor() successfully adds a sensor to the node.
     */
    @Test
    @DisplayName("addSensor() adds sensor to node")
    void testAddSensor() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");

      node.addSensor(sensor);

      assertEquals(1, node.getSensors().size());
      assertTrue(node.getSensors().contains(sensor));
    }

    /**
     * Verifies addActuator() successfully adds an actuator to the node.
     */
    @Test
    @DisplayName("addActuator() adds actuator to node")
    void testAddActuator() {
      HeaterActuator heater = new HeaterActuator("heater-01");

      node.addActuator(heater);

      assertEquals(1, node.getActuators().size());
      assertTrue(node.getActuators().contains(heater));
    }

    /**
     * Verifies addSensor() throws IllegalArgumentException for null sensor.
     */
    @Test
    @DisplayName("addSensor() throws exception for null sensor")
    void testAddNullSensor() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.addSensor(null);
      });
    }

    /**
     * Verifies addActuator() throws IllegalArgumentException for null actuator.
     */
    @Test
    @DisplayName("addActuator() throws exception for null actuator")
    void testAddNullActuator() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.addActuator(null);
      });
    }

    /**
     * Verifies multiple sensors can be added to the same node.
     */
    @Test
    @DisplayName("Add multiple sensors")
    void testAddMultipleSensors() {
      TemperatureSensor temp = new TemperatureSensor("temp-01");
      HumiditySensor humid = new HumiditySensor("humid-01");

      node.addSensor(temp);
      node.addSensor(humid);

      assertEquals(2, node.getSensors().size());
    }

    /**
     * Verifies multiple actuators can be added to the same node.
     */
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

  /**
   * Tests for removing devices from the SensorNode.
   */
  @Nested
  @DisplayName("Device Management - Remove")
  class RemoveDeviceTests {

    private SensorNode node;
    private TemperatureSensor sensor;
    private HeaterActuator actuator;

    /**
     * Initializes a SensorNode with one sensor and one actuator before each test.
     */
    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
      sensor = new TemperatureSensor("temp-01");
      actuator = new HeaterActuator("heater-01");
      node.addSensor(sensor);
      node.addActuator(actuator);
    }

    /**
     * Verifies removeSensor() successfully removes a sensor from the node.
     */
    @Test
    @DisplayName("removeSensor() removes sensor from node")
    void testRemoveSensor() {
      boolean removed = node.removeSensor(sensor);

      assertTrue(removed);
      assertEquals(0, node.getSensors().size());
    }

    /**
     * Verifies removeActuator() successfully removes an actuator from the node.
     */
    @Test
    @DisplayName("removeActuator() removes actuator from node")
    void testRemoveActuator() {
      boolean removed = node.removeActuator(actuator);

      assertTrue(removed);
      assertEquals(0, node.getActuators().size());
    }

    /**
     * Verifies removeSensor() returns false when attempting to remove a non-existent sensor.
     */
    @Test
    @DisplayName("removeSensor() returns false for non-existent sensor")
    void testRemoveNonExistentSensor() {
      TemperatureSensor other = new TemperatureSensor("temp-02");

      boolean removed = node.removeSensor(other);

      assertFalse(removed);
      assertEquals(1, node.getSensors().size());
    }

    /**
     * Verifies removeActuator() returns false when attempting to remove a non-existent actuator.
     */
    @Test
    @DisplayName("removeActuator() returns false for non-existent actuator")
    void testRemoveNonExistentActuator() {
      HeaterActuator other = new HeaterActuator("heater-02");

      boolean removed = node.removeActuator(other);

      assertFalse(removed);
      assertEquals(1, node.getActuators().size());
    }

    /**
     * Verifies removeSensor() throws IllegalArgumentException for null input.
     */
    @Test
    @DisplayName("removeSensor() throws exception for null")
    void testRemoveNullSensor() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.removeSensor(null);
      });
    }

    /**
     * Verifies removeActuator() throws IllegalArgumentException for null input.
     */
    @Test
    @DisplayName("removeActuator() throws exception for null")
    void testRemoveNullActuator() {
      assertThrows(IllegalArgumentException.class, () -> {
        node.removeActuator(null);
      });
    }
  }

  /**
   * Tests for protocol string generation.
   */
  @Nested
  @DisplayName("Protocol String Building")
  class ProtocolStringTests {

    private SensorNode node;

    /**
     * Initializes a fresh SensorNode before each test.
     */
    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
    }

    /**
     * Verifies getSensorSnapshot() returns empty string when no sensors are present.
     */
    @Test
    @DisplayName("getSensorSnapshot() with no sensors returns empty string")
    void testGetSensorSnapshotEmpty() {
      String snapshot = node.getSensorSnapshot();

      assertEquals("", snapshot);
    }

   /**
    * Verifies getSensorSnapshot() formats a single sensor correctly.
    *Expected format: "type#id:value"
    */
    @Test
    @DisplayName("getSensorSnapshot() with one sensor")
    void testGetSensorSnapshotOneSensor() {
      TemperatureSensor sensor = new TemperatureSensor("temp-01");
      node.addSensor(sensor);

      String snapshot = node.getSensorSnapshot();

      assertTrue(snapshot.startsWith("temperature#temp-01:"));
      assertTrue(snapshot.contains(":"));
    }

    /**
     * Verifies getSensorSnapshot() formats multiple sensors with comma separation.
     * Expected format: "type1#id1:value1,type2#id2:value2"
     */
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

    /**
     * Verifies getActuatorSnapshot() returns empty string when no actuators are present.
     */
    @Test
    @DisplayName("getActuatorSnapshot() with no actuators returns empty string")
    void testGetActuatorSnapshotEmpty() {
      String snapshot = node.getActuatorSnapshot();

      assertEquals("", snapshot);
    }

    /**
     * Verifies getActuatorSnapshot() formats an OFF actuator correctly.
     * OFF state is represented as ":0"
     */
    @Test
    @DisplayName("getActuatorSnapshot() with one actuator OFF")
    void testGetActuatorSnapshotOneActuatorOff() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      node.addActuator(heater);

      String snapshot = node.getActuatorSnapshot();

      assertEquals("heater#heater-01:0", snapshot);
    }

    /**
     * Verifies getActuatorSnapshot() formats an ON actuator correctly.
     *
     * ON state is represented as ":1"
     */
    @Test
    @DisplayName("getActuatorSnapshot() with one actuator ON")
    void testGetActuatorSnapshotOneActuatorOn() {
      HeaterActuator heater = new HeaterActuator("heater-01");
      heater.setState(true);
      node.addActuator(heater);

      String snapshot = node.getActuatorSnapshot();

      assertEquals("heater#heater-01:1", snapshot);
    }

    /**
     * Verifies getActuatorSnapshot() formats multiple actuators with comma separation.
     */
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

  /**
   * Tests for actuator search methods.
   */
  @Nested
  @DisplayName("Search Methods")
  class SearchTests {

    private SensorNode node;
    private HeaterActuator heater;
    private FanActuator fan;

    /**
     * Initializes a SensorNode with two actuators before each test.
     */
    @BeforeEach
    void setUp() {
      node = new SensorNode("node-01");
      heater = new HeaterActuator("heater-01");
      fan = new FanActuator("fan-01");
      node.addActuator(heater);
      node.addActuator(fan);
    }

    /**
     * Verifies findActuatorByType() performs case-insensitive search.
     */
    @Test
    @DisplayName("findActuatorByType() finds actuator case-insensitive")
    void testFindActuatorByType() {
      Actuator found = node.findActuatorByType("HEATER");

      assertNotNull(found);
      assertEquals(heater, found);
    }

    /**
     * Verifies findActuatorByType() works with lowercase input.
     */
    @Test
    @DisplayName("findActuatorByType() with lowercase")
    void testFindActuatorByTypeLowercase() {
      Actuator found = node.findActuatorByType("heater");

      assertNotNull(found);
      assertEquals(heater, found);
    }

    /**
     * Verifies findActuatorByType() returns null when actuator type is not found.
     */
    @Test
    @DisplayName("findActuatorByType() returns null for non-existent type")
    void testFindActuatorByTypeNotFound() {
      Actuator found = node.findActuatorByType("WINDOW");

      assertNull(found);
    }

    /**
     * Verifies findActuatorByType() returns null for null input.
     */
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
      sensor.readValue(); // Update sensor value after snapshot

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
      sensor.readValue(); // Update sensor value after snapshot
      double finalTemp = sensor.getCurrentValue();

      double delta = Math.abs(finalTemp - initialTemp);
      double driftThreshold = 0.3; // Acceptable drift due to random walk

      assertTrue(delta <= driftThreshold,
          "Temperature should be within acceptable drift when heater is OFF");
    }
  }
}