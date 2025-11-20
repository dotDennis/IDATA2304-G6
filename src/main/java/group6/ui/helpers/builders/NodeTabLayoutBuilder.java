package group6.ui.helpers.builders;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Builder responsible for composing the Node tab layout.
 * 
 * @author Dennis
 * @since 0.2.0
 */
public final class NodeTabLayoutBuilder {

  private final String nodeId;
  private final Label lastUpdateLabel;
  private final TitledPane sensorPane;
  private final TitledPane actuatorPane;

  private Runnable onAddSensor = null;
  private Runnable onAddActuator = null;
  private Runnable onRemoveSensor = null;
  private Runnable onRemoveActuator = null;
  private Runnable onDisconnect = null;
  private Runnable onCloseRequest = null;

  /** 
   * Creates a builder bound to the given node and existing panes. 
   */
  public NodeTabLayoutBuilder(String nodeId,
                              Label lastUpdateLabel,
                              TitledPane sensorPane,
                              TitledPane actuatorPane) {
    this.nodeId = nodeId;
    this.lastUpdateLabel = lastUpdateLabel;
    this.sensorPane = sensorPane;
    this.actuatorPane = actuatorPane;
  }

  /** 
   * Registers the callback for the "Add Sensor" button.
   */
  public NodeTabLayoutBuilder onAddSensor(Runnable action) {
    this.onAddSensor = action;
    return this;
  }

  /** 
   * Registers the callback for the "Add Actuator" button. 
   */
  public NodeTabLayoutBuilder onAddActuator(Runnable action) {
    this.onAddActuator = action;
    return this;
  }

  /** 
   * Registers the callback for the "Remove Sensor" button. 
   */
  public NodeTabLayoutBuilder onRemoveSensor(Runnable action) {
    this.onRemoveSensor = action;
    return this;
  }

  /** 
   * Registers the callback for the "Remove Actuator" button. 
   */
  public NodeTabLayoutBuilder onRemoveActuator(Runnable action) {
    this.onRemoveActuator = action;
    return this;
  }

  /** 
   * Registers the callback for the Disconnect button. 
   */
  public NodeTabLayoutBuilder onDisconnect(Runnable action) {
    this.onDisconnect = action;
    return this;
  }

  /** 
   * Registers the callback fired when the tab close button is pressed. 
   */
  public NodeTabLayoutBuilder onCloseRequest(Runnable action) {
    this.onCloseRequest = action;
    return this;
  }

  /** 
   * Builds and returns the configured Tab. 
   */
  public Tab build() {
    Tab tab = new Tab(nodeId);

    BorderPane content = new BorderPane();
    content.setPadding(new Insets(10));
    content.getStyleClass().add("card");

    TitledPane sensorSection = sensorPane;
    sensorSection.setMaxHeight(Double.MAX_VALUE);
    TitledPane actuatorSection = actuatorPane;
    actuatorSection.setMaxHeight(Double.MAX_VALUE);

    HBox statusRow = new HBox();
    statusRow.setSpacing(8);
    statusRow.getChildren().add(lastUpdateLabel);

    VBox paneStack = new VBox(12, statusRow, sensorSection, actuatorSection);
    VBox.setVgrow(sensorSection, Priority.ALWAYS);
    VBox.setVgrow(actuatorSection, Priority.ALWAYS);
    content.setCenter(paneStack);

    HBox bottomBar = new HBox(10);
    bottomBar.setPadding(new Insets(10, 0, 0, 0));
    bottomBar.getStyleClass().add("button-row");

    Button addSensorButton = new Button("Add Sensor");
    addSensorButton.setOnAction(e -> run(onAddSensor));

    Button addActuatorButton = new Button("Add Actuator");
    addActuatorButton.setOnAction(e -> run(onAddActuator));

    Button removeSensorButton = new Button("Remove Sensor");
    removeSensorButton.setOnAction(e -> run(onRemoveSensor));

    Button removeActuatorButton = new Button("Remove Actuator");
    removeActuatorButton.setOnAction(e -> run(onRemoveActuator));

    Button disconnectButton = new Button("Disconnect");
    disconnectButton.setOnAction(e -> run(onDisconnect));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    bottomBar.getChildren().addAll(
        addSensorButton,
        addActuatorButton,
        removeSensorButton,
        removeActuatorButton,
        spacer,
        disconnectButton
    );
    content.setBottom(bottomBar);

    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setStyle("-fx-background-color: transparent;");

    tab.setContent(scrollPane);
    tab.setOnCloseRequest(e -> run(onCloseRequest));
    return tab;
  }

  /** Safely executes optional callbacks. */
  private void run(Runnable action) {
    if (action != null) {
      action.run();
    }
  }
}
