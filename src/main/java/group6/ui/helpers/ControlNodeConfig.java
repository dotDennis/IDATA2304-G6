package group6.ui.helpers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import group6.entity.node.ControlPanel;

/**
 * Configuration for multiple {@link ControlPanel} instances.
 * <p>
 * The configuration is loaded from JSON as a list of {@link Entry}
 * objects. Each entry is converted into a {@link ControlNode}, which couples
 * the configuration data with its {@link ControlPanel}.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public final class ControlNodeConfig {

  /**
   * A simple data entry representing a configured control node in JSON.
   * <p>
   * These values are deserialized directly from JSON.
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

    public long getRefreshInterval() {
      return refreshInterval <= 0 ? 1000 : refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
      this.refreshInterval = refreshInterval;
    }

    public List<SensorNodeEntry> getSensorNodes() {
      if (sensorNodes == null) {
        sensorNodes = new ArrayList<>();
      }
      return sensorNodes;
    }

    public void setSensorNodes(List<SensorNodeEntry> sensorNodes) {
      this.sensorNodes = sensorNodes;
    }
  }

  @JsonPropertyOrder({ "id", "host", "port", "refreshInterval", "sensors", "actuators" })
  public static class SensorNodeEntry {
    private String id;
    private String host = "localhost";
    private int port;
    private long refreshInterval = 5000;
    private List<SensorEntry> sensors = new ArrayList<>();
    private List<DeviceEntry> actuators = new ArrayList<>();

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public long getRefreshInterval() {
      return refreshInterval <= 0 ? 5000 : refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
      this.refreshInterval = refreshInterval;
    }

    public List<SensorEntry> getSensors() {
      if (sensors == null) {
        sensors = new ArrayList<>();
      }
      return sensors;
    }

    public void setSensors(List<SensorEntry> sensors) {
      this.sensors = sensors;
    }

    public List<DeviceEntry> getActuators() {
      if (actuators == null) {
        actuators = new ArrayList<>();
      }
      return actuators;
    }

    public void setActuators(List<DeviceEntry> actuators) {
      this.actuators = actuators;
    }
  }

  public static class DeviceEntry {
    private String id;
    private String type;

    public DeviceEntry() {
    }

    public DeviceEntry(String id, String type) {
      this.id = id;
      this.type = type;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  public static class SensorEntry extends DeviceEntry {
    private long updateIntervalMs = 5000;
    private Double lastValue;
    private String lastUpdated;

    public SensorEntry() {
    }

    public SensorEntry(String id, String type, long updateIntervalMs) {
      super(id, type);
      this.updateIntervalMs = updateIntervalMs;
    }

    public long getUpdateIntervalMs() {
      return updateIntervalMs <= 0 ? 5000 : updateIntervalMs;
    }

    public void setUpdateIntervalMs(long updateIntervalMs) {
      this.updateIntervalMs = updateIntervalMs;
    }

    public Double getLastValue() {
      return lastValue;
    }

    public void setLastValue(Double lastValue) {
      this.lastValue = lastValue;
    }

    public String getLastUpdated() {
      return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
      this.lastUpdated = lastUpdated;
    }
  }

  /**
   * Wraps a configuration {@link Entry} with a {@link ControlPanel}
   * created from an entry.
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
