package group6;

import group6.ui.GreenhouseGuiApp;

/**
 * Entry point for the application.
 * Launches JavaFX GUI application
 */
public class Main {

  /**
   * Main method to launch the GUI application.
   * 
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    try {
      GreenhouseGuiApp.main(args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to start ControlPanel GUI demo", e);
    }
  }
}
