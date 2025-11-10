package group6.entity.node;

import java.util.EnumSet;

/**
 * Abstract base class for all SMG nodes.
 * Each node has a unique ID and a fixed node type (SENSOR or CONTROL).
 * The node type is final once set.
 */
public abstract class Node {

    private String nodeId;
    private final NodeType nodeType;

    /**
     * Default constructor for dynamically assigned node IDs.
     * Subclasses must define the node type.
     *
     * @param nodeType the type of node (SENSOR or CONTROL)
     */
    protected Node(NodeType nodeType) {
        this.nodeType = validateNodeType(nodeType);
    }

    /**
     * Constructor for nodes with predefined ID and type.
     *
     * @param nodeId   unique identifier
     * @param nodeType node type (SENSOR or CONTROL)
     */
    protected Node(String nodeId, NodeType nodeType) {
        this.nodeType = validateNodeType(nodeType);
        setNodeId(nodeId);
    }

    // ---------- Helper ----------

    /**
     * Ensures the node type is valid and recognized by the system.
     *
     * @param type node type to validate
     * @return the validated node type
     * @throws IllegalArgumentException if the node type is null or not a known enum
     *                                  constant
     */
    private static NodeType validateNodeType(NodeType type) {
        if (type == null) {
            throw new IllegalArgumentException("nodeType cannot be null");
        }
        if (!EnumSet.allOf(NodeType.class).contains(type)) {
            throw new IllegalArgumentException("Invalid nodeType: " + type);
        }
        return type;
    }

    // ---------- Setter with validation ----------
    private void setNodeId(String nodeId) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("nodeId cannot be null or blank");
        }
        this.nodeId = nodeId.trim();
    }

    // ---------- Getters ----------
    public String getNodeId() {
        return nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

}