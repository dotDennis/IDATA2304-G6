package group6.ui.helpers;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import group6.entity.node.ControlPanel;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for multiple {@link ControlPanel} instances.
 * 
 * <p>The configuration is loaded from JSON as a list of {@link Entry}
 * objects. Each entry is converted into a {@link ControlNode}, which couples
 * the configuration data with its {@link ControlPanel}.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public final class ControlNodeConfig {

  /**
   * A simple data entry representing a configured control node in JSON.
   * 
   * <p>These values are deserialized directly from JSON.
   */
  public static class Entry {
    private String id;
    private String displayName;
    private long refreshInterval = 1000;
    private List<SensorNodeEntry> sensorNodes = new ArrayList<>();

    /**
     * Gets the identifier for this control node.
     * 
     * @return the id
     */
    public String getId() {
      return id;
    }

    /**
     * sets the identifier for this control node.
     * 
     * @param id the id to set
     */
    public void setId(String id) {
      this.id = id;
    }

    /**
     * Gets the String display name for this control node.
     * 
     * @return String label - used when presenting this node in the UI
     */
    public String getDisplayName() {
      return displayName;
    }

    /**
     * Sets the display name for this control node.
     * 
     * @param displayName label used when presenting this node in the UI
     */
    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    /**
     * Gets the refresh interval for this control node.
     * 
     * @return the refresh interval in milliseconds
     */
    public long getRefreshInterval() {
      return refreshInterval <= 0 ? 1000 : refreshInterval;
    }

    /**
     * Sets the refresh interval for this control node.
     * 
     * @param refreshInterval the refresh interval in milliseconds
     */
    public void setRefreshInterval(long refreshInterval) {
      this.refreshInterval = refreshInterval;
    }

    /**
     * Gets the list of sensor node entries for this control node.
     * 
     * @return the sensor node entries
     */
    public List<SensorNodeEntry> getSensorNodes() {
      if (sensorNodes == null) {
        sensorNodes = new ArrayList<>();
      }
      return sensorNodes;
    }

    /**
     * Sets the list of sensor node entries for this control node.
     * 
     * @param sensorNodes the sensor node entries to set
     */
    public void setSensorNodes(List<SensorNodeEntry> sensorNodes) {
      this.sensorNodes = sensorNodes;
    }
  }

  /**
   * Nested class for sensor node entries.
   * 
   * <p>Represents a configured sensor node within a control node.
   * 
   * <p>https://stackoverflow.com/questions/19272830/order-of-json-objects-using-jacksons-objectmapper
   */
  @JsonPropertyOrder({ "id", "host", "port", "refreshInterval", "sensors", "actuators" })
  public static class SensorNodeEntry {
    private String id;
    private String host = "localhost";
    private int port;
    private long refreshInterval = 5000;
    private List<SensorEntry> sensors = new ArrayList<>();
    private List<DeviceEntry> actuators = new ArrayList<>();

    // ------- Getters and Setters -------

    /**
     * Gets the sensor node ID.
     * 
     * @return the node ID
     */
    public String getId() {
      return id;
    }

    /**
     * Sets the sensor node ID.
     * 
     * @param id the node ID to set
     */
    public void setId(String id) {
      this.id = id;
    }

    /**
     * Gets the host address.
     * 
     * @return the host address
     */
    public String getHost() {
      return host;
    }

    /**
     * Sets the host address.
     * 
     * @param host the host address to set
     */
    public void setHost(String host) {
      this.host = host;
    }

    /**
     * Gets the port number.
     * 
     * @return the port number
     */
    public int getPort() {
      return port;
    }

    /**
     * Sets the port number.
     * 
     * @param port the port number to set
     */
    public void setPort(int port) {
      this.port = port;
    }

    /**
     * Gets the refresh interval.
     * 
     * @return the refresh interval in milliseconds
     */
    public long getRefreshInterval() {
      return refreshInterval <= 0 ? 5000 : refreshInterval;
    }

    /**
     * Sets the refresh interval.
     * 
     * @param refreshInterval the refresh interval in milliseconds
     */
    public void setRefreshInterval(long refreshInterval) {
      this.refreshInterval = refreshInterval;
    }

    /**
     * Gets the list of configured sensors.
     * 
     * @return the list of sensors
     */
    public List<SensorEntry> getSensors() {
      if (sensors == null) {
        sensors = new ArrayList<>();
      }
      return sensors;
    }

    /**
     * Sets the list of configured sensors.
     * 
     * @param sensors the list of sensors to set
     */
    public void setSensors(List<SensorEntry> sensors) {
      this.sensors = sensors;
    }

    /**
     * Gets the list of configured actuators.
     * 
     * @return the list of actuators
     */
    public List<DeviceEntry> getActuators() {
      if (actuators == null) {
        actuators = new ArrayList<>();
      }
      return actuators;
    }

    /**
     * Sets the list of configured actuators.
     * 
     * @param actuators the list of actuators to set
     */
    public void setActuators(List<DeviceEntry> actuators) {
      this.actuators = actuators;
    }
  }

  // ------- Helper Classes -------

  /**
   * Nested class for device entries.
   * 
   * <p>Represents a configured device (sensor or actuator) within a sensor node.
   */
  public static class DeviceEntry {
    private String id;
    private String type;

    /**
     * Default constructor.
     * 
     * <p>Needed for JSON deserialization.
     */
    public DeviceEntry() {
    }

    /**
     * Parameterized constructor.
     * 
     * @param id   the device id
     * @param type the device type
     */
    public DeviceEntry(String id, String type) {
      this.id = id;
      this.type = type;
    }

    /**
     * Gets the device id.
     * 
     * @return the device id
     */
    public String getId() {
      return id;
    }

    /**
     * Sets the device id.
     * 
     * @param id the device id to set
     */
    public void setId(String id) {
      this.id = id;
    }

    /**
     * Gets the device type.
     * 
     * @return the device type
     */
    public String getType() {
      return type;
    }

    /**
     * Sets the device type.
     * 
     * @param type the device type to set
     */
    public void setType(String type) {
      this.type = type;
    }
  }

  /**
   * Nested class for sensor entries.
   * 
   * <p>Represents a configured sensor within a sensor node.
   */
  public static class SensorEntry extends DeviceEntry {
    private long updateIntervalMs = 5000;

    /**
     * Default constructor.
     * 
     * <p>Needed for JSON deserialization.
     */
    public SensorEntry() {
    }

    /**
     * Parameterized constructor.
     * 
     * @param id               the sensor id
     * @param type             the sensor type
     * @param updateIntervalMs the update interval in milliseconds
     */
    public SensorEntry(String id, String type, long updateIntervalMs) {
      super(id, type);
      this.updateIntervalMs = updateIntervalMs;
    }

    /**
     * Gets the update interval in milliseconds.
     * 
     * @return the update interval in milliseconds
     */
    public long getUpdateIntervalMs() {
      return updateIntervalMs <= 0 ? 5000 : updateIntervalMs;
    }

    /**
     * Sets the update interval in milliseconds.
     * 
     * @param updateIntervalMs the update interval in milliseconds
     */
    public void setUpdateIntervalMs(long updateIntervalMs) {
      this.updateIntervalMs = updateIntervalMs;
    }

  }

  /**
   * A configured control node instance.
   * 
   * <p>Couples a configuration {@link Entry} with its associated
   * {@link ControlPanel}.
   */
  public static class ControlNode {

    private final Entry entry;
    private final ControlPanel panel;

    /**
     * Creates a new node from the configuration entry.
     *
     * @param entry configuration describing the node
     */
    ControlNode(Entry entry) {
      this.entry = entry;
      this.panel = new ControlPanel(entry.getId());
    }

    /**
     * Returns the configuration for this node.
     *
     * @return the entry
     */
    public Entry getEntry() {
      return entry;
    }

    /**
     * Returns the control panel associated with this node.
     * 
     * @return the control panel
     */
    public ControlPanel getPanel() {
      return panel;
    }
  }

  private final List<Entry> entries;
  private final List<ControlNode> nodes;

  /**
   * Creates a new configuration with the given nodes.
   *
   * @param nodes list of configured control nodes
   */
  private ControlNodeConfig(List<Entry> entries, List<ControlNode> nodes) {
    this.entries = entries;
    this.nodes = nodes;
  }

  /**
   * Returns all configured control nodes.
   *
   * @return list of control nodes
   */
  public List<ControlNode> getNodes() {
    return nodes;
  }

  public List<Entry> getEntries() {
    return entries;
  }

  /**
   * Builds a {@link ControlNodeConfig} from JSON-deserialized entries.
   *
   * @param entries list of configuration entries
   * @return a configuration with one {@link ControlNode} per entry
   */
  public static ControlNodeConfig fromEntries(List<Entry> entries) {
    List<Entry> entryCopies = new ArrayList<>();
    List<ControlNode> nodes = new ArrayList<>();
    if (entries != null) {
      for (Entry entry : entries) {
        entryCopies.add(entry);
        nodes.add(new ControlNode(entry));
      }
    }
    return new ControlNodeConfig(entryCopies, nodes);
  }
}
