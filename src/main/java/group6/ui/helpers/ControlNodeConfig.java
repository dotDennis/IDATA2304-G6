package group6.ui.helpers;

import java.util.ArrayList;
import java.util.List;

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

  private final List<ControlNode> nodes;

  /**
   * Creates a new configuration with the given nodes.
   *
   * @param nodes list of configured control nodes
   */
  private ControlNodeConfig(List<ControlNode> nodes) {
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

  /**
   * Builds a {@link ControlNodeConfig} from JSON-deserialized entries.
   *
   * @param entries list of configuration entries
   * @return a configuration with one {@link ControlNode} per entry
   */
  public static ControlNodeConfig fromEntries(List<Entry> entries) {
    List<ControlNode> nodes = new ArrayList<>(entries.size());
    for (Entry entry : entries) {
      nodes.add(new ControlNode(entry));
    }
    return new ControlNodeConfig(nodes);
  }
}