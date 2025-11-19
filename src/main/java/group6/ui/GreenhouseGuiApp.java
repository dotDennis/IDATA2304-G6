package group6.ui;

import java.io.IOException;
import java.util.List;

import group6.ui.helpers.ControlNodeConfig;
import group6.ui.helpers.ControlNodeLoader;
import group6.ui.views.MainView;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX application entry point. Hosts multiple control-panel workspaces and a
 * demo sensor node server.
 */
public class GreenhouseGuiApp extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(GreenhouseGuiApp.class);

  private MainView mainView;

  @Override
  public void start(Stage primaryStage) {
    LOGGER.info("Starting Greenhouse Application");

    ControlNodeConfig config = loadControlNodes();
    mainView = new MainView(config);

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

  private ControlNodeConfig loadControlNodes() {
    try {
      return ControlNodeLoader.loadFromResource("control-nodes.json");
    } catch (IOException e) {
      LOGGER.warn("Failed to load control node config, using fallback", e);
      ControlNodeConfig.Entry fallback = new ControlNodeConfig.Entry();
      fallback.setId("control-01");
      fallback.setDisplayName("Control Node");
      return ControlNodeConfig.fromEntries(List.of(fallback));
    }
  }

  private void shutdown() {
    if (mainView != null) {
      mainView.shutdown();
    }
    LOGGER.info("Application shutdown successfully");
  }

  public static void main(String[] args) {
    launch(args);
  }
}
