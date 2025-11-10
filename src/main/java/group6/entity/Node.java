package group6.entity;

public abstract class Node {

    private String nodeId;
    private NodeType nodeType;

    /**
     * Default constructor for cases where node properties are set later.
     */
    protected Node() {
    }

    protected Node(String nodeId, NodeType nodeType) {
        setNodeId(nodeId);
        setNodeType(nodeType);
    }

    // ---------- Setters with validation ----------
    private void setNodeId(String nodeId) {
        if (nodeId == null || nodeId.isBlank()) {
            throw new IllegalArgumentException("nodeId cannot be null or blank");
        }
        this.nodeId = nodeId.trim();
    }

    private void setNodeType(NodeType nodeType) {
        if (nodeType == null) {
            throw new IllegalArgumentException("nodeType cannot be null");
        }
        this.nodeType = nodeType;
    }

    // ---------- Getters ----------
    public String getNodeId() {
        return nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }


}