package group6.ui;

import java.nio.file.Path;
import java.nio.file.Paths;

import group6.ui.helpers.ControlNodeConfig;
import group6.ui.helpers.ControlNodePersistenceService;
import group6.ui.views.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX application entry point. Hosts multiple control-panel workspaces and a
 * demo sensor node server.
 */
public class GreenhouseGuiApp extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(GreenhouseGuiApp.class);
  private static final Path CONFIG_FILE = Paths.get("resources/config.json");

  private MainView mainView;
  private ControlNodePersistenceService persistenceService;

  @Override
  public void start(Stage primaryStage) {
    LOGGER.info("Starting Greenhouse Application");

    persistenceService = new ControlNodePersistenceService(CONFIG_FILE);
    ControlNodeConfig config = persistenceService.load();
    mainView = new MainView(config, persistenceService::autoSave);

    Scene scene = mainView.getScene();
    scene.getStylesheets().add(GreenhouseGuiApp.class.getResource("/global.css").toExternalForm());

    primaryStage.setTitle("Greenhouse Control Panel");
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(event -> {
      LOGGER.info("Exiting Greenhouse Application");
      shutdown();
    });
    primaryStage.show();
    LOGGER.info("Greenhouse Application started successfully");
  }

  private void shutdown() {
    if (mainView != null) {
      ControlNodeConfig config = mainView.exportConfig();
      persistenceService.save(config);
      mainView.shutdown();
    }
    LOGGER.info("Application shutdown successfully");
  }

  public static void main(String[] args) {
    launch(args);
  }
}
