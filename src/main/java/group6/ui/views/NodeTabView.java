package group6.ui.views;

import group6.ui.controllers.GuiController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a single node tab containing sensor data and actuator controls.
 * Each tab corresponds to one connected sensor node.
 */
public class NodeTabView {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeTabView.class);

  private final String nodeId;
  private final GuiController controller;
  private final Tab tab;
  private final SensorDataView sensorDataView;
  private final ActuatorControlView actuatorControlView;
  private final Runnable onCloseCallback;

  /**
   * Creates a new node tab view.
   *
   * @param nodeId         the ID of the sensor node
   * @param controller     the GUI controller
   * @param onCloseCallback callback to invoke when tab is closed
   */
  public NodeTabView(String nodeId, GuiController controller, Runnable onCloseCallback) {
    this.nodeId = nodeId;
    this.controller = controller;
    this.onCloseCallback = onCloseCallback;

    // Create views for this specific node
    this.sensorDataView = new SensorDataView(controller);
    this.sensorDataView.setNodeId(nodeId);

    this.actuatorControlView = new ActuatorControlView(controller);
    this.actuatorControlView.setNodeId(nodeId);

    // Create the tab
    this.tab = createTab();

    LOGGER.info("Created tab for node: {}", nodeId);
  }

  /**
   * Creates the JavaFX Tab with all content.
   *
   * @return configured Tab
   */
  private Tab createTab() {
    Tab newTab = new Tab(nodeId);

    // Main content
    VBox content = new VBox(15);
    content.setPadding(new Insets(10));

    // Add sensor and actuator views
    content.getChildren().addAll(
            sensorDataView.getView(),
            actuatorControlView.getView()
    );

    // Bottom bar with disconnect button
    HBox bottomBar = new HBox(10);
    bottomBar.setPadding(new Insets(10, 0, 0, 0));

    // Spacer to push button to the right
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button disconnectButton = new Button("Disconnect");
    disconnectButton.setOnAction(e -> handleDisconnect());

    bottomBar.getChildren().addAll(spacer, disconnectButton);
    content.getChildren().add(bottomBar);

    newTab.setContent(content);

    // Handle tab close via 'X' button
    newTab.setOnCloseRequest(e -> {
      LOGGER.info("User requested to close tab for node: {}", nodeId);
      handleDisconnect();
    });

    return newTab;
  }

  /**
   * Handles disconnection from this node.
   */
  private void handleDisconnect() {
    try {
      controller.disconnectNode(nodeId);
      LOGGER.info("Disconnected from node: {}", nodeId);

      // Invoke callback to remove tab from TabPane
      if (onCloseCallback != null) {
        onCloseCallback.run();
      }
    } catch (Exception e) {
      LOGGER.error("Failed to disconnect from node: {}", nodeId, e);
    }
  }

  /**
   * Refreshes both sensor and actuator views.
   * Called by auto-refresh timer.
   */
  public void refresh() {
    sensorDataView.refresh();
    actuatorControlView.refresh();
  }

  /**
   * Gets the JavaFX Tab.
   *
   * @return the tab
   */
  public Tab getTab() {
    return tab;
  }

  /**
   * Gets the node ID.
   *
   * @return the node ID
   */
  public String getNodeId() {
    return nodeId;
  }
}