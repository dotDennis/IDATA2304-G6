package group6.ui.views;

import group6.entity.node.ControlPanel;
import group6.ui.controllers.GuiController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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

  /**
   * Creates the actuator control view
   *
   * @param controller the GUI controller
   */
  public ActuatorControlView(GuiController controller) {
    this.controller = controller;
    this.contentBox = new VBox();
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

    contentBox.getChildren().clear();

    if(data == null) {
      Label noData = new Label("No data available. Connect to a Node first.");
      noData.setStyle("-fx-text-fill: gray");
      contentBox.getChildren().add(noData);
      return;
    }

    Map<String, Boolean> actuators = data.getActuatorStates();

    if(actuators.isEmpty()) {
      Label waiting = new Label("Waiting for actuator data...");
      waiting.setStyle("-fx-text-fill: gray");
      contentBox.getChildren().add(waiting);
      return;
    }

    //Display each actuator with controls
    for (Map.Entry<String, Boolean> entry: actuators.entrySet()) {
      HBox actuatorRow = createActuatorControl(entry.getKey(), entry.getValue());
      contentBox.getChildren().add(actuatorRow);
    }
  }

  /**
   * Creates a control row for an actuator.
   *
   * @param actuatorType the actuator type.
   * @param currentState the current state (ON, OFF)
   * @return HBox containing the actuator control
   */
  private HBox createActuatorControl(String actuatorType, boolean currentState){
    HBox row = new HBox(10);
    row.setAlignment(Pos.CENTER);

    //Actuator name with icons
    String icon = getActuatorIcon(actuatorType);
    Label nameLabel = new Label(icon + " " + capitalize(actuatorType));
    nameLabel.setPrefWidth(150);
    nameLabel.setFont(Font.font(14));

    //RadioButtons for ON/OFF
    ToggleGroup group = new ToggleGroup();
    RadioButton onButton = new RadioButton("ON");
    RadioButton offButton = new RadioButton("OFF");
    onButton.setToggleGroup(group);
    offButton.setToggleGroup(group);

    //Sets current state
    if (currentState) {
      onButton.setSelected(true);
    }else {
      offButton.setSelected(true);
    }

    //Add listener to send commands
    onButton.setOnAction(event -> {
      if(onButton.isSelected()) {
        sendCommand(actuatorType, true);
      }
    });

    offButton.setOnAction(event -> {
      if(offButton.isSelected()) {
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
      controller.sendCommand(currentNodeId, actuatorType, state);
      LOGGER.info("Sent command via GUI: {} = {}", actuatorType, state);
    }catch (Exception e) {
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
