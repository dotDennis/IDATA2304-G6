package group6.ui.helpers;

import javafx.scene.control.Alert;

/**
 * Simple helper for showing alerts from UI views.
 */
public final class UiAlerts {

  private UiAlerts() {
  }

  public static void info(String title, String message) {
    show(title, message, Alert.AlertType.INFORMATION);
  }

  public static void error(String title, String message) {
    show(title, message, Alert.AlertType.ERROR);
  }

  private static void show(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
