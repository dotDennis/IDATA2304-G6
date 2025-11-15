package group6.ui;

import group6.entity.node.ControlPanel;
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
 */
public class GreenhouseGuiApp extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(GreenhouseGuiApp.class);
  private GuiController controller;

  @Override
  public void start(Stage primaryStage) {
    LOGGER.info("Starting Greenhouse Application");

    ControlPanel controlPanel = new ControlPanel("control-01");
    //Controller and main view
    controller = new GuiController(controlPanel);
    MainView mainView = new MainView(controller);

    //Setup
    primaryStage.setTitle("Greenhouse Control Panel");
    primaryStage.setScene(mainView.getScene());
    primaryStage.setOnCloseRequest(event -> {
      LOGGER.info("Exiting Greenhouse Application");
    });
    primaryStage.show();
    LOGGER.info("Greenhouse Application started successfully");
  }

  /**
   * Shutdown hook to clean up resources
   */
  private void shutdown() {
    if (controller != null) {
      controller.shutdown();
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
