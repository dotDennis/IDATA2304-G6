package group6.ui.views;

import group6.entity.node.ControlPanel;
import group6.protocol.RefreshTarget;
import group6.ui.controllers.GuiController;
import group6.ui.helpers.DevicePresentation;
import group6.ui.helpers.ToggleActuatorRow;
import group6.ui.helpers.UiAlerts;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for actuator controls.
 * Allows user to control actuators
 * 
 * @author fidjor, dotDennis
 * @since 0.2.0
 */
public class ActuatorControlView {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorControlView.class);

  private final GuiController controller;
  private final TitledPane view;
  private final VBox contentBox;
  private String currentNodeId = "sensor-01"; // default
  private final Map<String, ToggleActuatorRow> actuatorRows = new HashMap<>();
  private boolean uiBuilt = false;
  private long lastCommandTime = 0;
  private static final long COMMAND_COOLDOWN = 400; // to avoid rapid toggles and UI flicker

  /**
   * Creates the actuator control view
   *
   * @param controller the GUI controller
   */
  public ActuatorControlView(GuiController controller) {
    this.controller = controller;
    this.contentBox = new VBox(10);
    this.contentBox.setPadding(new Insets(10));

    this.view = new TitledPane();
    this.view.setText("🔧 Actuator Controls");
    this.view.setContent(contentBox);
    this.view.setExpanded(true);

    refresh();
  }

  /** 
   * Refreshes the actuator control display using latest node data. 
   */
  public void refresh() {
    ControlPanel.NodeData data = controller.getNodeData(currentNodeId);

    if (data == null) {
      showPlaceholder("No data available. Connect to a node first.");
      return;
    }

    Map<String, Boolean> actuators = data.getActuatorStates();

    if (actuators.isEmpty()) {
      showPlaceholder("Waiting for actuator data...");
      return;
    }

    if (shouldRebuild(actuators)) {
      buildUI(actuators);
      uiBuilt = true;
    } else {
      long timeSinceCommand = System.currentTimeMillis() - lastCommandTime;
      if (timeSinceCommand > COMMAND_COOLDOWN) {
        updateButtonStates(actuators);
      }
    }
  }

  /** 
   * Displays a placeholder label when no actuator data exists. 
   * */
  private void showPlaceholder(String message) {
    if (contentBox.getChildren().size() == 1 &&
        contentBox.getChildren().get(0) instanceof Label) {
      Label existing = (Label) contentBox.getChildren().get(0);
      if (existing.getText().equals(message)) {
        return;
      }
    }

    contentBox.getChildren().clear();
    actuatorRows.clear();
    uiBuilt = false;

    Label label = new Label(message);
    label.setStyle("-fx-text-fill: gray");
    contentBox.getChildren().add(label);
  }

  /** 
   * Rebuilds actuator rows when the server list changes. 
   */
  private void buildUI(Map<String, Boolean> actuators) {
    contentBox.getChildren().clear();
    actuatorRows.clear();
    uiBuilt = false;

    for (Map.Entry<String, Boolean> entry : actuators.entrySet()) {
      String actuatorType = entry.getKey();
      boolean state = entry.getValue();

      ToggleActuatorRow row = new ToggleActuatorRow(state, desired -> sendCommand(actuatorType, desired));
      actuatorRows.put(actuatorType, row);
      updateActuatorLabel(actuatorType);
      contentBox.getChildren().add(row.getRoot());
    }
  }

  /**
   *  Applies toggle updates to existing rows.
   *  */
  private void updateButtonStates(Map<String, Boolean> actuators) {
    for (Map.Entry<String, Boolean> entry : actuators.entrySet()) {
      String actuatorType = entry.getKey();
      boolean state = entry.getValue();
      ToggleActuatorRow row = actuatorRows.get(actuatorType);
      if (row != null) {
        row.applyServerState(state);
        updateActuatorLabel(actuatorType);
      }
    }
  }

  /** 
   * Detects when the actuator set has changed.
   */
  private boolean shouldRebuild(Map<String, Boolean> actuators) {
    if (!uiBuilt) {
      return true;
    }
    if (actuators.size() != actuatorRows.size()) {
      return true;
    }
    for (String key : actuators.keySet()) {
      if (!actuatorRows.containsKey(key)) {
        return true;
      }
    }
    return false;
  }

  /** 
   * Sends a toggle command for the given actuator key.
   */
  private void sendCommand(String actuatorType, boolean state) {
    try {
      lastCommandTime = System.currentTimeMillis(); // Record command time
      DevicePresentation presentation = DevicePresentation.fromRawKey(actuatorType);
      String commandTarget = presentation.getDeviceId().isEmpty()
          ? presentation.getBaseType()
          : presentation.getDeviceId();
      controller.sendCommand(currentNodeId, commandTarget, state);
      controller.requestNodeRefresh(currentNodeId, RefreshTarget.ACTUATORS);
      LOGGER.info("Sent command via GUI: {} = {}", actuatorType, state);
      ToggleActuatorRow row = actuatorRows.get(actuatorType);
      if (row != null) {
        row.markPending(state);
      }
    } catch (Exception e) {
      LOGGER.error("Failed to send command via GUI", e);
      ToggleActuatorRow row = actuatorRows.get(actuatorType);
      if (row != null) {
        row.clearPending();
      }
      UiAlerts.error("Actuator Control", "Command failed: " + e.getMessage());
    }
  }

  /**
   * Sets the node ID to control.
   *
   * @param nodeId the node ID.
   */
  public void setNodeId(String nodeId) {
    this.currentNodeId = nodeId;
    refresh();
  }

  /**
   * Gets the view component.
   *
   * @return TitledPane view
   */
  public TitledPane getView() {
    return view;
  }

  // Helper methods

  /** 
   * Maps base actuator types to emoji icons.
   * 
   * @param type the base actuator type
   * @return the corresponding emoji icon, as String
   */
  private String getActuatorIcon(String type) {
    return switch (type.toLowerCase()) {
      case "heater" -> "🔥";
      case "fan" -> "🌀";
      case "window_opener" -> "🪟";
      case "valve" -> "🚰";
      case "door_lock" -> "🔒";
      case "light_switch" -> "💡";
      default -> "🔧";
    };
  }

  /** 
   * Updates the text for a single actuator row.
   */
  private void updateActuatorLabel(String actuatorKey) {
    ToggleActuatorRow row = actuatorRows.get(actuatorKey);
    if (row == null) {
      return;
    }
    DevicePresentation presentation = DevicePresentation.fromRawKey(actuatorKey);
    String deviceId = presentation.getDeviceId();
    String label = deviceId.isEmpty()
        ? presentation.getBaseType()
        : deviceId;
    String icon = getActuatorIcon(presentation.getBaseType());
    row.setDisplayText(icon + " " + label);
  }


}
