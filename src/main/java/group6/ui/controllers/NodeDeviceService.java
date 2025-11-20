package group6.ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.actuator.Actuator;
import group6.entity.device.sensor.Sensor;
import group6.protocol.RefreshTarget;
import group6.ui.helpers.EmbeddedSensorNodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * Service for managing devices on sensornodes within the GUI.
  * <p>
  * Provides methods to add, remove, and list sensors and actuators on nodes,
  * and triggers refresh requests to update the GUI accordingly.
  * 
  * @author dotDennis
  * @since 0.2.0
 */
public class NodeDeviceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeDeviceService.class);

  private final GuiController controller;
  private final EmbeddedSensorNodeManager manager;

  /**
   * Creates a new {@code NodeDeviceService} instance.
   *
   * @param controller the GUI controller
   * @param manager the node manager
   */
  public NodeDeviceService(GuiController controller, EmbeddedSensorNodeManager manager) {
    this.controller = controller;
    this.manager = manager;
  }

  /**
   * Adds a sensor to the specified node and triggers a sensor refresh request.
   *
   * @param nodeId the ID of the target node
   * @param type the sensor type to create
   * @param deviceId the unique device identifier
   * @param intervalMs the sensor update interval in milliseconds
   *
   * @return an empty optional on success, or an error message on failure
   */
  public Optional<String> addSensor(String nodeId, SensorType type, String deviceId, long intervalMs) {
    try {
      manager.addSensor(nodeId, type, deviceId, intervalMs);
      requestRefresh(nodeId, RefreshTarget.SENSORS);
      return Optional.empty();
    } catch (Exception e) {
      LOGGER.warn("Failed to add sensor {}: {}", deviceId, e.getMessage());
      return Optional.of("Failed to add sensor: " + e.getMessage());
    }
  }

  /**
   * Adds an actuator to the specified node and triggers an actuator refresh.
   *
   * @param nodeId the ID of the target node
   * @param type the actuator type to create
   * @param deviceId the unique device identifier
   *
   * @return an empty on success, or an error message on failure
   */
  public Optional<String> addActuator(String nodeId, ActuatorType type, String deviceId) {
    try {
      manager.addActuator(nodeId, type, deviceId);
      requestRefresh(nodeId, RefreshTarget.ACTUATORS);
      return Optional.empty();
    } catch (Exception e) {
      LOGGER.warn("Failed to add actuator {}: {}", deviceId, e.getMessage());
      return Optional.of("Failed to add actuator: " + e.getMessage());
    }
  }

  /**
   * Removes a sensor from a node and triggers a sensor refresh if successful.
   *
   * @param nodeId the ID of the target node
   * @param deviceId the ID of the sensor to remove
   *
   * @return true if the sensor was removed, or false otherwise
   */
  public boolean removeSensor(String nodeId, String deviceId) {
    boolean removed = manager.removeSensor(nodeId, deviceId);
    if (removed) {
      requestRefresh(nodeId, RefreshTarget.SENSORS);
    }
    return removed;
  }

  /**
   * Removes an actuator from a node and triggers an actuator refresh if successful.
   * 
   * @param nodeId the ID of the target node
   * @param deviceId the ID of the actuator to remove
   *
   * @return true if the actuator was removed, or false otherwise
   */
  public boolean removeActuator(String nodeId, String deviceId) {
    boolean removed = manager.removeActuator(nodeId, deviceId);
    if (removed) {
      requestRefresh(nodeId, RefreshTarget.ACTUATORS);
    }
    return removed;
  }

  /**
   * Returns a copy of the sensors associated with the given node.
   *
   * @param nodeId the ID of the target node
   *
   * @return a list of sensors for the specified node
   */
  public List<Sensor> listSensors(String nodeId) {
    return new ArrayList<>(manager.listSensors(nodeId));
  }

  /**
   * Returns a copy of the actuators associated with the given node.
   *
   * @param nodeId the ID of the target node
   *
   * @return a list of actuators for the specified node
   */
  public List<Actuator> listActuators(String nodeId) {
    return new ArrayList<>(manager.listActuators(nodeId));
  }

  /**
   * Sends a refresh request to the GUI controller for the given node and target.
   */
  private void requestRefresh(String nodeId, RefreshTarget target) {
    try {
      controller.requestNodeRefresh(nodeId, target);
    } catch (RuntimeException e) {
      LOGGER.warn("Refresh request failed for {}: {}", nodeId, e.getMessage());
    }
  }
}
