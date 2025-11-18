package group6.ui;

import group6.entity.device.actuator.FanActuator;
import group6.entity.device.actuator.HeaterActuator;
import group6.entity.device.sensor.HumiditySensor;
import group6.entity.device.sensor.TemperatureSensor;
import group6.entity.node.ControlPanel;
import group6.entity.node.SensorNode;
import group6.net.TcpServer;
import group6.ui.controllers.GuiController;
import group6.ui.views.MainView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX application for Greenhouse control panel.
 * Entry point for the GUI application.
 * 
 * @
 *
 */
public class GreenhouseGuiApp extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(GreenhouseGuiApp.class);

  private GuiController controller;
  private TcpServer demoServer;
  private MainView mainView;

  @Override
  public void start(Stage primaryStage) {
    LOGGER.info("Starting Greenhouse Application");

    startDemoServer();

    //Create backend control
    ControlPanel controlPanel = new ControlPanel("control-01");

    //Controller and main view
    controller = new GuiController(controlPanel);
    mainView = new MainView(controller);

    //Start auto refresh (every 2 seconds)
    controller.startAutoRefresh(() -> mainView.refreshAllTabs(), 2000);

    //Setup
    primaryStage.setTitle("Greenhouse Control Panel");
    primaryStage.setScene(mainView.getScene());
    primaryStage.setOnCloseRequest(event -> {
      LOGGER.info("Exiting Greenhouse Application");
      shutdown();
    });
    primaryStage.show();
    LOGGER.info("Greenhouse Application started successfully");
  }

  /**
   * Starts a demo SensorNode server in background
   */
  private void startDemoServer() {
    try {
      SensorNode node = new SensorNode("sensor-01");

      // Add demo actuators fist
      HeaterActuator heater = new HeaterActuator("heater-1");
      FanActuator fan = new FanActuator("fan-1");
      node.addActuator(heater);
      node.addActuator(fan);

      // Add demo sensors that react to actuators
      node.addSensor(new TemperatureSensor("temp-1"));

      node.addSensor(new HumiditySensor("hum-1"));

      // Start TCP server
      demoServer = new TcpServer(12345, node);
      Thread serverThread = new Thread(() -> {
        try {
          demoServer.start();
        } catch (Exception e) {
          LOGGER.error("Demo server error", e);
        }
      }, "DemoServer");
      serverThread.setDaemon(true);
      serverThread.start();

      LOGGER.info("Demo SensorNode server started on port 12345");
    } catch (Exception e) {
      LOGGER.error("Failed to start demo server", e);
    }
  }

  /**
   * Shutdown hook to clean up resources
   */
  private void shutdown() {
    if (controller != null) {
      controller.shutdown();
    }
    if(demoServer != null) {
      demoServer.stop();
    }
    LOGGER.info("Application shutdown successfully");
  }

  /**
   * Main entry point.
   *
   * @param args command line arguments
   */
  public static void main (String[] args) {
    launch(args);
  }
}
