package group6.ui.controllers;

import group6.entity.node.ControlPanel;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for the GUI application
 * Coordinates between backend and GUI views
 */
public class GuiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GuiController.class);

  private final ControlPanel controlPanel;
  private Timer refreshTimer;

  /**
   * Creates a new GUI controller
   *
   * @param controlPanel the backend control panel.
   * @throws IllegalArgumentException if controlPanel is null.
   */
  public GuiController(ControlPanel controlPanel) {
    if(controlPanel == null) {
      throw new IllegalArgumentException("ControlPanel cannot be null");
    }
    this.controlPanel = controlPanel;
    LOGGER.info ("GuiController intialized with ControlPanel");
  }

  /**
   * Connects to a sensor node
   *
   * @param nodeId the node ID.
   * @param host the host address
   * @param port the port number
   * @throws RuntimeException if connection fails
   */
  public void connectToNode(String nodeId, String host, int port) {
    try{
      controlPanel.connectToSensorNode(nodeId, host, port);
      LOGGER.info("Connected to {} at {}:{}", nodeId, host, port);
    } catch (Exception e) {
      LOGGER.error("Failed to connect to ", e);
      throw new RuntimeException("Connection failed: " + e.getMessage(), e );
    }
  }

  /**
   * Sends a command to control an actuator.
   *
   * @param nodeId the node ID.
   * @param actuatorType the actuator type.
   * @param state the desired state (ON, OFF)
   * @throws RuntimeException if command fails
   */
  public void sendCommand (String nodeId, String actuatorType, boolean state) {
    try{
      controlPanel.sendCommand(nodeId, actuatorType, state);
      LOGGER.info("Send command: {} = {}",actuatorType, state);
    }catch (Exception e) {
      LOGGER.error("Failed to send command", e);
      throw new RuntimeException("Send command failed: " + e.getMessage(), e );
    }
  }

  /**
   * Gets cached node data.
   *
   * @param nodeId the node ID.
   * @return the cached data or null if not available.
   */
  public ControlPanel.NodeData getNodeData (String nodeId) {
    return controlPanel.getNodeData(nodeId);
  }

  /**
   * Starts periodic refresh.
   *
   * @param updateCallback callback to run on each refresh.
   * @param intervalMs refresh interval in milliseconds.
   */
  public void startAutoRefresh(Runnable updateCallback, long intervalMs){
    if(refreshTimer != null) {
      refreshTimer.cancel();
    }

    refreshTimer = new Timer(true);
    refreshTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(updateCallback);
      }
    }, intervalMs, intervalMs);

    LOGGER.info("Auto refresh started with interval {} ms", intervalMs);
  }

  /**
   * Stops the auto-refresh timer.
   */
  public void stopAutoRefresh(){
    if(refreshTimer != null) {
      refreshTimer.cancel();
      refreshTimer = null;
      LOGGER.info("Auto refresh stopped");
    }
  }


  /**
   * Shuts down the controller and control panel
   */
  public void shutdown() {
    stopAutoRefresh();
    if(controlPanel != null) {
      controlPanel.shutdown();
    }
    LOGGER.info("GuiController shut down");
  }
}
