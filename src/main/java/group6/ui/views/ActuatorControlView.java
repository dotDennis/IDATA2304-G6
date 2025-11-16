package group6.ui.views;

import group6.entity.node.ControlPanel;
import group6.ui.controllers.GuiController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for actuator controls.
 * Allows user to control actuators
 */
public class ActuatorControlView {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorControlView.class);

  private final GuiController controller;
  private final TitledPane view;
  private final VBox contentBox;
  private String currentNodeId = "sensor-01"; //default

  //Cache UI components to update them instead of rebuilding
  private final Map<String, RadioButton> onButtons = new HashMap<>();
  private final Map<String, RadioButton> offButtons = new HashMap<>();
  private boolean uiBuilt = false;
  private long lastCommandTime = 0;
  private static final long COMMAND_COOLDOWN = 1000; // 1 second

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
   * Refreshes the actuator control display
   * Called periodically by auto-refresh or manually
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

    // Build UI once
    if (!uiBuilt) {
      buildUI(actuators);
      uiBuilt = true;
    } else {
      // Don't update buttons immediately after sending command
      long timeSinceCommand = System.currentTimeMillis() - lastCommandTime;
      if (timeSinceCommand > COMMAND_COOLDOWN) {
        updateButtonStates(actuators);
      }
    }
  }

  private void showPlaceholder(String message) {
    if (contentBox.getChildren().size() == 1 &&
            contentBox.getChildren().get(0) instanceof Label) {
      Label existing = (Label) contentBox.getChildren().get(0);
      if (existing.getText().equals(message)) {
        return;
      }
    }

    contentBox.getChildren().clear();
    onButtons.clear();
    offButtons.clear();
    uiBuilt = false;

    Label label = new Label(message);
    label.setStyle("-fx-text-fill: gray");
    contentBox.getChildren().add(label);
  }

  private void buildUI(Map<String, Boolean> actuators) {
    contentBox.getChildren().clear();
    onButtons.clear();
    offButtons.clear();

    for (Map.Entry<String, Boolean> entry : actuators.entrySet()) {
      String actuatorType = entry.getKey();
      boolean state = entry.getValue();

      HBox row = createActuatorControl(actuatorType, state);
      contentBox.getChildren().add(row);
    }
  }

  private void updateButtonStates(Map<String, Boolean> actuators) {
    for (Map.Entry<String, Boolean> entry : actuators.entrySet()) {
      String actuatorType = entry.getKey();
      boolean state = entry.getValue();

      RadioButton onButton = onButtons.get(actuatorType);
      RadioButton offButton = offButtons.get(actuatorType);

      if (onButton != null && offButton != null) {
        // Temporarily remove listeners to avoid triggering commands
        onButton.setOnAction(null);
        offButton.setOnAction(null);

        // Update selection
        if (state) {
          onButton.setSelected(true);
        } else {
          offButton.setSelected(true);
        }

        // Re-add listeners
        onButton.setOnAction(e -> {
          if (onButton.isSelected()) {
            sendCommand(actuatorType, true);
          }
        });

        offButton.setOnAction(e -> {
          if (offButton.isSelected()) {
            sendCommand(actuatorType, false);
          }
        });
      }
    }
  }

  /**
   * Creates a control row for an actuator.
   *
   * @param actuatorType the actuator type.
   * @param currentState the current state (ON, OFF)
   * @return HBox containing the actuator control
   */
  private HBox createActuatorControl(String actuatorType, boolean currentState) {
    HBox row = new HBox(10);
    row.setAlignment(Pos.CENTER_LEFT);

    String icon = getActuatorIcon(actuatorType);
    Label nameLabel = new Label(icon + " " + capitalize(actuatorType));
    nameLabel.setPrefWidth(150);
    nameLabel.setFont(Font.font(14));

    ToggleGroup group = new ToggleGroup();
    RadioButton onButton = new RadioButton("ON");
    RadioButton offButton = new RadioButton("OFF");
    onButton.setToggleGroup(group);
    offButton.setToggleGroup(group);

    // Store references
    onButtons.put(actuatorType, onButton);
    offButtons.put(actuatorType, offButton);

    // Set current state
    if (currentState) {
      onButton.setSelected(true);
    } else {
      offButton.setSelected(true);
    }

    // Add listeners
    onButton.setOnAction(e -> {
      if (onButton.isSelected()) {
        sendCommand(actuatorType, true);
      }
    });

    offButton.setOnAction(e -> {
      if (offButton.isSelected()) {
        sendCommand(actuatorType, false);
      }
    });

    row.getChildren().addAll(nameLabel, onButton, offButton);
    return row;
  }

  /**
   * Sends command to control an actuator
   *
   * @param actuatorType the actuator type.
   * @param state the desired state
   */
  private void sendCommand(String actuatorType, boolean state) {
    try {
      lastCommandTime = System.currentTimeMillis(); // Record command time
      controller.sendCommand(currentNodeId, actuatorType, state);
      LOGGER.info("Sent command via GUI: {} = {}", actuatorType, state);
    } catch (Exception e) {
      LOGGER.error("Failed to send command via GUI", e);
      showError("Command failed: " + e.getMessage());
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

  //Helper methods
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

  private String capitalize(String str) {
    if(str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase()
            + str.substring(1).replace("_"," ");
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
