package group6.ui.views;

import group6.entity.device.ActuatorType;
import group6.entity.device.Device;
import group6.entity.device.SensorType;
import group6.entity.node.ControlPanel;
import group6.entity.node.RefreshTarget;
import group6.ui.controllers.GuiController;
import group6.ui.controllers.NodeDeviceService;
import group6.ui.helpers.DeviceDialogBuilder;

import group6.ui.helpers.NodeTabLayoutBuilder;
import group6.ui.helpers.RemovalDialogBuilder;
import group6.ui.helpers.UiAlerts;
import group6.protocol.DeviceKey;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a single node tab containing sensor data and actuator controls.
 * Each tab corresponds to one connected sensor node.
 * 
 * @author fidjor, dotDennis
 * @since 0.2.0
 */
public class NodeTabView {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeTabView.class);

  private final String nodeId;
  private final GuiController controller;
  private final NodeDeviceService deviceService;
  private final Tab tab;
  private final SensorDataView sensorDataView;
  private final ActuatorControlView actuatorControlView;
  private final Label lastUpdateLabel;
  private final Runnable onCloseCallback;
  private final Runnable onConfigChanged;

  /**
   * Creates a new node tab view.
   *
   * @param nodeId         the ID of the sensor node
   * @param controller     the GUI controller
   * @param onCloseCallback callback to invoke when tab is closed
   */
  public NodeTabView(String nodeId,
                     GuiController controller,
                     NodeDeviceService deviceService,
                     Runnable onCloseCallback,
                     Runnable onConfigChanged) {
    this.nodeId = nodeId;
    this.controller = controller;
    this.deviceService = deviceService;
    this.onCloseCallback = onCloseCallback;
    this.onConfigChanged = onConfigChanged;

    // Create views for this specific node
    this.sensorDataView = new SensorDataView(controller);
    this.sensorDataView.setNodeId(nodeId);

    this.actuatorControlView = new ActuatorControlView(controller);
    this.actuatorControlView.setNodeId(nodeId);

    this.lastUpdateLabel = new Label();
    updateLastUpdateLabel();

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
    Tab builtTab = new NodeTabLayoutBuilder(
        nodeId,
        lastUpdateLabel,
        sensorDataView.getView(),
        actuatorControlView.getView())
        .onAddSensor(this::handleAddSensor)
        .onAddActuator(this::handleAddActuator)
        .onRemoveSensor(this::handleRemoveSensor)
        .onRemoveActuator(this::handleRemoveActuator)
        .onDisconnect(this::handleDisconnect)
        .onCloseRequest(() -> {
          LOGGER.info("User requested to close tab for node: {}", nodeId);
          handleDisconnect();
        })
        .build();

    builtTab.setOnSelectionChanged(event -> {
      if (builtTab.isSelected()) {
        refresh();
        requestFullRefresh();
      }
    });

    return builtTab;
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
    updateLastUpdateLabel();
  }

  private void requestFullRefresh() {
    try {
      controller.requestNodeRefresh(nodeId, RefreshTarget.ALL);
    } catch (RuntimeException e) {
      LOGGER.warn("Refresh request failed for {}: {}", nodeId, e.getMessage());
    }
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

  /** 
   * Opens the add-sensor dialog and processes the result. 
   */
  private void handleAddSensor() {
    DeviceDialogBuilder.showDeviceDialogWithInterval(
        "Add Sensor",
        SensorType.values(),
        this::suggestSensorId,
        (type, deviceId, intervalMs) -> runDeviceAddition(
            () -> deviceService.addSensor(nodeId, type, deviceId, intervalMs),
            sensorDataView::refresh),
        5000);
  }

  /** 
   * Opens the add-actuator dialog and processes the result. 
   */
  private void handleAddActuator() {
    DeviceDialogBuilder.showDeviceDialog(
        "Add Actuator",
        ActuatorType.values(),
        this::suggestActuatorId,
        (type, deviceId) -> runDeviceAddition(
            () -> deviceService.addActuator(nodeId, type, deviceId),
            actuatorControlView::refresh));
  }

  /**
   * Executes a device addition action, refreshes the view on success, and
   * propagates any validation error from the dialog.
   */
  private Optional<String> runDeviceAddition(Supplier<Optional<String>> additionAction,
                                             Runnable onSuccess) {
    Optional<String> error = additionAction.get();
    if (error.isPresent()) {
      return error;
    }
    onSuccess.run();
    notifyConfigChanged();
    return Optional.empty();
  }

  /** 
   * Launches the removal dialog for sensors. 
   */
  private void handleRemoveSensor() {
    List<DeviceOption> options = buildDeviceOptions(deviceService.listSensors(nodeId));
    handleDeviceRemoval(
        "Remove Sensor",
        "No sensors available to remove.",
        options,
        option -> deviceService.removeSensor(nodeId, option.id()),
        sensorDataView::refresh,
        option -> controller.clearSensorFromCache(nodeId, option.typeKey()),
        "Could not remove sensor: ");
  }

  /** 
   * Launches the removal dialog for actuators. 
   */
  private void handleRemoveActuator() {
    List<DeviceOption> options = buildDeviceOptions(deviceService.listActuators(nodeId));
    handleDeviceRemoval(
        "Remove Actuator",
        "No actuators available to remove.",
        options,
        option -> deviceService.removeActuator(nodeId, option.id()),
        actuatorControlView::refresh,
        option -> controller.clearActuatorFromCache(nodeId, option.typeKey()),
        "Could not remove actuator: ");
  }

  /** 
   * Shared helper that runs the removal flow for sensors or actuators. 
   */
  private void handleDeviceRemoval(String title,
                                   String emptyMessage,
                                   List<DeviceOption> options,
                                   Function<DeviceOption, Boolean> removalAction,
                                   Runnable refreshAction,
                                   Consumer<DeviceOption> cacheClearAction,
                                   String failurePrefix) {

    showDeviceRemovalDialog(title, emptyMessage, options, option -> {
      boolean removed = removalAction.apply(option);
      if (removed) {
        refreshAction.run();
        notifyConfigChanged();
        if (cacheClearAction != null) {
          cacheClearAction.accept(option);
        }
      } else {
        UiAlerts.error("Device Management", failurePrefix + option.id());
      }
    });
  }

  /** 
   * Shows the selection dialog for removing a device. 
   */
  private void showDeviceRemovalDialog(String title,
                                       String emptyMessage,
                                       List<DeviceOption> options,
                                       Consumer<DeviceOption> removalHandler) {
    if (options.isEmpty()) {
      UiAlerts.info("Device Management", emptyMessage);
      return;
    }
    RemovalDialogBuilder.show(title, options, option -> removalHandler.accept(option));
  }

  /** 
   * Suggests a sequential sensor ID for the given type. 
   */
  private String suggestSensorId(SensorType type) {
    return suggestDeviceId(deviceService.listSensors(nodeId), type);
  }

  /** 
   * Suggests a sequential actuator ID for the given type.
   */
  private String suggestActuatorId(ActuatorType type) {
    return suggestDeviceId(deviceService.listActuators(nodeId), type);
  }

  /** 
   * Counts existing devices of the same type to build the next ID.
   */
  private String suggestDeviceId(List<? extends Device<?>> devices, Enum<?> type) {
    long count = devices.stream()
        .filter(device -> type.equals(device.getDeviceType()))
        .count();
    return type.name().toLowerCase(Locale.ROOT) + "-" + (count + 1);
  }

  /** 
   * Simple record to represent device options in removal dialog.
  */
  private record DeviceOption(String id, String label, String typeKey) {
    @Override
    public String toString() {
      return label;
    }
  }

  /** 
   * Converts domain devices into dialog options. 
  */
  private List<DeviceOption> buildDeviceOptions(List<? extends Device<?>> devices) {
    List<DeviceOption> options = new ArrayList<>();
    for (Device<?> device : devices) {
      DeviceKey key = DeviceKey.of(device.getDeviceTypeName(), device.getDeviceId());
      String label = device.getDeviceId() + " (" + key.getType() + ")";
      options.add(new DeviceOption(device.getDeviceId(), label, key.toProtocolKey()));
    }
    return options;
  }

  /** 
   * Notifies listeners that the node/device configuration changed. 
   */
  private void notifyConfigChanged() {
    if (onConfigChanged != null) {
      onConfigChanged.run();
    }
  }

  /** 
   * Updates the status label with the latest refresh timestamp.
   */
  private void updateLastUpdateLabel() {
    ControlPanel.NodeData data = controller.getNodeData(nodeId);
    if (data == null) {
      lastUpdateLabel.setText("Last update: no data");
      lastUpdateLabel.setStyle("-fx-text-fill: gray;");
      return;
    }
    long secondsAgo = (System.currentTimeMillis() - data.getLastUpdate()) / 1000;
    String text = "Last update: " + secondsAgo + "s ago";
    if (secondsAgo > 10) {
      text += " (stale)";
      lastUpdateLabel.setStyle("-fx-text-fill: #b71c1c;");
    } else {
      lastUpdateLabel.setStyle("-fx-text-fill: #4b5563;");
    }
    lastUpdateLabel.setText(text);
  }
}
