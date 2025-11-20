package group6.ui.helpers;

import javafx.scene.control.Alert;

/**
 * Simple helper for showing alerts from UI views.
 */
public final class UiAlerts {

  /**
   * Private constructor to prevent instantiation.
   */
  private UiAlerts() {
  }

  /**
   * Shows an information alert.
   * 
   * @param title   the title of the alert
   * @param message the message of the alert
   */
  public static void info(String title, String message) {
    show(title, message, Alert.AlertType.INFORMATION);
  }

  /**
   * Shows an error alert.
   * 
   * @param title   the title of the alert
   * @param message the message of the alert
   */
  public static void error(String title, String message) {
    show(title, message, Alert.AlertType.ERROR);
  }

  /**
   * Private helper to show an alert.
   * 
   * @param title   the title
   * @param message the message
   * @param type    the alert type
   */
  private static void show(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
