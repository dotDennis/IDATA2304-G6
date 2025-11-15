package group6.ui;

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

  @Override
  public void start(Stage primaryStage) {
    LOGGER.info("Starting Greenhouse");

    //Controller and main view
    GuiController controller = new GuiController();
    MainView mainView = new MainView(controller);

    //Setup
    primaryStage.setTitle("Greenhouse Control Panel");
    primaryStage.setScene(mainView.getScene());
    primaryStage.setOnCloseRequest(event -> {
      LOGGER.info("Exiting Greenhouse");
    });
    primaryStage.show();
    LOGGER.info("Greenhouse started successfully");
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
