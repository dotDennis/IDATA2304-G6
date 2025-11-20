package group6.ui.views;

import java.util.HashMap;
import java.util.Map;

import group6.protocol.RefreshTarget;
import group6.ui.controllers.EmbeddedNodeService;
import group6.ui.controllers.GuiController;
import group6.ui.controllers.NodeDeviceService;
import group6.ui.helpers.ControlNodeConfig;
import group6.ui.helpers.EmbeddedSensorNodeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Encapsulates the UI controls for a single control panel node.
 * <p>
 * Handles embedded sensor node creation, tab management, and
 * device operations via the {@link NodeDeviceService}.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public class ControlPanelWorkspace {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControlPanelWorkspace.class);

  private final GuiController controller;
  private final EmbeddedNodeService nodeService;
  private final EmbeddedSensorNodeManager sensorNodeManager;
  private final NodeDeviceService nodeDeviceService;
  private final long refreshInterval;
  private final Runnable configChangeListener;
  private boolean suppressNotifications = false;
  private final BorderPane root;
  private final Label statusLabel;
  private final TabPane tabPane;
  private final Map<String, NodeTabView> nodeTabs;
  private final ScrollPane scrollPane;
  private final VBox centerContent;
  private final TextField nodeIdField;
  private final TextField hostField;
  private final TextField portField;
  private final TextField refreshField;

  /**
   * Creates a workspace with a default refresh interval.
   */
  public ControlPanelWorkspace(GuiController controller) {
    this(controller, 1000, null);
  }

  /**
   * Creates a workspace with the provided refresh interval.
   */
  public ControlPanelWorkspace(GuiController controller, long refreshInterval) {
    this(controller, refreshInterval, null);
  }

  /**
   * Creates a workspace with optional config-change listener.
   */
  public ControlPanelWorkspace(GuiController controller, long refreshInterval, Runnable configChangeListener) {
    this.controller = controller;
    this.nodeService = new EmbeddedNodeService(controller);
    this.sensorNodeManager = nodeService.getManager();
    this.nodeDeviceService = new NodeDeviceService(controller, sensorNodeManager);
    this.refreshInterval = refreshInterval > 0 ? refreshInterval : 1000;
    this.configChangeListener = configChangeListener;
    this.root = new BorderPane();
    this.statusLabel = new Label();
    this.tabPane = new TabPane();
    this.nodeTabs = new HashMap<>();
    this.centerContent = createCenterContent();
    this.scrollPane = createScrollPane(centerContent);
    this.nodeIdField = createTextField("sensornode-01", 140);
    this.hostField = createTextField("localhost", 120);
    this.portField = createTextField("12345", 90);
    this.refreshField = createTextField("5000", 100);

    setupLayout();
  }

  /**
   * Builds the static layout of the workspace.
   */
  private void setupLayout() {
    root.setPadding(new Insets(15));

    Label title = new Label("Sensor Nodes");
    title.getStyleClass().add("section-title");
    BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
    root.setTop(title);

    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

    HBox nodeCreator = buildNodeCreator();

    VBox card = new VBox(12, nodeCreator, tabPane);
    card.getStyleClass().add("card");
    VBox.setVgrow(tabPane, Priority.ALWAYS);

    centerContent.getChildren().add(card);
    root.setCenter(scrollPane);

    showStatus("Ready", false);
    root.setBottom(statusLabel);
  }

  /**
   * Handles node creation form submission.
   */
  private void createEmbeddedNode() {
    NodeCreationRequest request = readNodeCreationRequest();
    if (request == null) {
      return;
    }
    String nodeId = request.nodeId();
    try {
      if (sensorNodeManager.hasNode(nodeId)) {
        showStatus("Node " + nodeId + " already exists.", true);
        return;
      }
      EmbeddedSensorNodeManager.EmbeddedNode node = nodeService.createAndConnect(
          nodeId, request.host(), request.port(), request.refreshMs());
      attachEmbeddedNodeTab(node);
      showStatus("Connected to embedded " + nodeId, false);
      notifyConfigChanged();
    } catch (Exception e) {
      showStatus("Failed to create node: " + e.getMessage(), true);
    }
  }

  /**
   * Adds a new node tab for the given embedded node.
   */
  private void attachEmbeddedNodeTab(EmbeddedSensorNodeManager.EmbeddedNode node) {
    String nodeId = node.getNodeId();
    if (nodeTabs.containsKey(nodeId)) {
      return;
    }
    NodeTabView nodeTabView = new NodeTabView(nodeId, controller, nodeDeviceService,
        () -> removeEmbeddedNodeTab(nodeId), configChangeListener);
    nodeTabs.put(nodeId, nodeTabView);
    tabPane.getTabs().add(nodeTabView.getTab());
    nodeTabView.refresh();
    tabPane.getSelectionModel().select(nodeTabView.getTab());
  }

  /**
   * Removes the tab and disconnects the embedded node.
   */
  public void removeEmbeddedNodeTab(String nodeId) {
    NodeTabView nodeTabView = nodeTabs.remove(nodeId);
    if (nodeTabView != null) {
      tabPane.getTabs().remove(nodeTabView.getTab());
      nodeService.removeNode(nodeId);
      showStatus("Disconnected from " + nodeId, false);
      notifyConfigChanged();
    }
  }

  /**
   * Refreshes every node tab (called by auto-refresh timer).
   */
  public void refreshAllTabs() {
    for (NodeTabView nodeTabView : nodeTabs.values()) {
      nodeTabView.refresh();
    }
  }

  /**
   * @return the root node to attach in the scene graph.
   */
  public Node getRoot() {
    return root;
  }

  /**
   * Restores nodes from persisted config without spamming listeners.
   */
  public void restore(ControlNodeConfig.Entry config) {
    suppressNotifications = true;
    nodeService.restoreNodes(config, node -> {
      attachEmbeddedNodeTab(node);
      requestInitialRefresh(node.getNodeId());
    }, message -> showStatus(message, true));
    suppressNotifications = false;
  }

  /**
   * Requests an initial ALL refresh for a newly restored node.
   */
  private void requestInitialRefresh(String nodeId) {
    try {
      controller.requestNodeRefresh(nodeId, RefreshTarget.ALL);
    } catch (RuntimeException e) {
      LOGGER.warn("Initial refresh request failed for {}: {}", nodeId, e.getMessage());
    }
  }

  /**
   * Serializes the workspace state back into a config entry.
   */
  public ControlNodeConfig.Entry toConfigEntry(String id, String displayName) {
    return nodeService.buildConfigEntry(id, displayName, refreshInterval);
  }

  /**
   * Shuts down embedded nodes and controller resources.
   */
  public void shutdown() {
    sensorNodeManager.shutdown();
    controller.shutdown();
  }

  /**
   * Notifies listeners if config changes and notifications are not suppressed.
   */
  private void notifyConfigChanged() {
    if (!suppressNotifications && configChangeListener != null) {
      configChangeListener.run();
    }
  }

  /**
   * Updates the bottom status bar with messages/errors.
   */
  private void showStatus(String message, boolean error) {
    statusLabel.setText(message);
    String style = error
        ? "-fx-background-color: #fdecea; -fx-text-fill: #b71c1c; -fx-padding: 8; -fx-font-weight: bold;"
        : "-fx-background-color: #eef2ff; -fx-text-fill: #4338ca; -fx-padding: 8; -fx-font-weight: normal;";
    statusLabel.setStyle(style);
  }

  /**
   * Creates the VBox that holds the card layout.
   */
  private VBox createCenterContent() {
    VBox box = new VBox(15);
    box.setPadding(new Insets(10));
    box.setFillWidth(true);
    return box;
  }

  /**
   * Wraps the center content in a scroll pane and wires resizing.
   */
  private ScrollPane createScrollPane(VBox content) {
    ScrollPane pane = new ScrollPane(content);
    pane.setFitToWidth(true);
    pane.viewportBoundsProperty()
        .addListener((obs, oldBounds, newBounds) -> content.setMinHeight(newBounds.getHeight()));
    return pane;
  }

  /**
   * Convenience factory for uniform text fields in the form.
   */
  private TextField createTextField(String defaultValue, double width) {
    TextField field = new TextField(defaultValue);
    field.setPrefWidth(width);
    return field;
  }

  /**
   * Builds the input row for adding embedded nodes.
   */
  private HBox buildNodeCreator() {
    HBox row = new HBox(10);
    Button createButton = new Button("Add Sensor Node");
    createButton.setOnAction(e -> createEmbeddedNode());
    row.getChildren().addAll(
        new Label("ID:"), nodeIdField,
        new Label("IP:"), hostField,
        new Label("Port:"), portField,
        new Label("Refresh (ms):"), refreshField,
        createButton);
    row.getStyleClass().add("input-row");
    return row;
  }

  /**
   * Reads/validates form inputs, returning null if invalid.
   */
  private NodeCreationRequest readNodeCreationRequest() {
    String nodeId = nodeIdField.getText().trim();
    String host = hostField.getText().trim();
    String portText = portField.getText().trim();
    String refreshText = refreshField.getText().trim();
    if (nodeId.isEmpty() || host.isEmpty() || portText.isEmpty() || refreshText.isEmpty()) {
      showStatus("Node ID, IP, port and refresh are required.", true);
      return null;
    }
    try {
      int port = Integer.parseInt(portText);
      long refreshMs = Long.parseLong(refreshText);
      if (refreshMs <= 0) {
        refreshMs = 5000;
      }
      return new NodeCreationRequest(nodeId, host, port, refreshMs);
    } catch (NumberFormatException e) {
      showStatus("Invalid port or refresh value.", true);
      return null;
    }
  }

  private record NodeCreationRequest(String nodeId, String host, int port, long refreshMs) {
  }
}
