package group6.ui.views;

import group6.entity.node.ControlPanel;
import group6.ui.controllers.GuiController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Map;

/**
 * View for displaying sensor data.
 * Shows real time sensor readings from connected nodes
 */
public class SensorDataView {

  private final GuiController controller;
  private final TitledPane view;
  private final VBox contentBox;
  private String currentNodeId = "sensor-01"; // This is the default node that displays

  /**
   * Creates sensor data view.
   *
   * @param controller the GUI controller
   */
  public SensorDataView(GuiController controller) {
    this.controller = controller;
    this.contentBox = new VBox(10);
    this.contentBox.setPadding(new Insets(10));

    this.view = new TitledPane();
    this.view.setText("Sensor Data");
    this.view.setContent(contentBox);
    this.view.setExpanded(true);

    refresh();
  }

  /**
   * Refreshes the sensordata display.
   * Called periodically by auto-refresh or manually.
   */
  public void refresh() {
    ControlPanel.NodeData data = controller.getNodeData(currentNodeId);

    contentBox.getChildren().clear();

    if(data == null) {
      Label noData = new Label("No data available. Connect to a node first");
      noData.setStyle("-fx-text-fill: gray");
      contentBox.getChildren().add(noData);
      return;
    }

    Map<String, Double> sensors = data.getSensorReadings();

    if(sensors.isEmpty()) {
      Label waiting = new Label("Waiting for sensor data...");
      waiting.setStyle("-fx-text-fill: gray");
      contentBox.getChildren().add(waiting);
      return;
    }

    //Display each sensor reading
    for (Map.Entry<String, Double> entry : sensors.entrySet()) {
      String baseType = extractBaseType(entry.getKey());
      String deviceId = extractDeviceId(entry.getKey());
      String icon = getSensorIcon(baseType);
      String unit = getSensorUnit(baseType);
      String name = capitalize(baseType);
      if (!deviceId.isEmpty()) {
        name += " (" + deviceId + ")";
      }

      long updatedAt = data.getSensorUpdatedAt(entry.getKey());
      String lastUpdateText = formatAgo(updatedAt);

      Label sensorLabel = new Label(String.format("%s %s: %.2f %s (%s)", icon, name,
              entry.getValue(), unit, lastUpdateText));
      sensorLabel.setFont(Font.font(14));

      contentBox.getChildren().add(sensorLabel);
    }
  }

  /**
   * Sets the node ID to display for.
   *
   * @param nodeId the node ID
   */
  public void setNodeId(String nodeId) {
    this.currentNodeId = nodeId;
    refresh();
  }

  /**
   * Gets the view component.
   *
   * @return the TitledPane view
   */
  public TitledPane getView() {
    return view;
  }

  //Helper methods
  private String getSensorIcon(String type){
    return switch (type.toLowerCase()) {
      case "temperature" -> "🌡️";
      case "humidity" -> "💧";
      case "light" -> "☀️";
      case "ph" -> "⚗️";
      case "wind_speed" -> "💨";
      case "fertilizer" -> "🌱";
      default -> "📊";
    };
  }

  private String getSensorUnit(String type){
    return switch (type.toLowerCase()){
      case "temperature" -> "°C";
      case "humidity" -> "%";
      case "light" -> "lux";
      case "ph" -> "pH";
      case "wind_speed" -> "m/s";
      case "fertilizer" -> "mg/L";
      default -> "";
    };
  }
  private String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase()
            + str.substring(1).replace("_", " ");
  }

  private String formatAgo(long timestamp) {
    if (timestamp <= 0) {
      return "never";
    }
    long seconds = Math.max(0, (System.currentTimeMillis() - timestamp) / 1000);
    if (seconds < 1) {
      return "just now";
    }
    return seconds + "s ago";
  }

  private String extractBaseType(String key){
    if (key == null) {
      return "";
    }
    int idx = key.indexOf('#');
    return idx >= 0 ? key.substring(0, idx) : key;
  }

  private String extractDeviceId(String key){
    if (key == null) {
      return "";
    }
    int idx = key.indexOf('#');
    if (idx >= 0 && idx < key.length() - 1) {
      return key.substring(idx + 1);
    }
    return "";
  }
}
