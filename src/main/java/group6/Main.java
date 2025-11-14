package group6;

import group6.ui.ControlPanelTuiDemo;

/**
 * Entry point for the application. Delegates to the ControlPanelTuiDemo so
 * running the project immediately launches the interactive demo.
 */
public class Main {
  public static void main(String[] args) {
    try {
      ControlPanelTuiDemo.main(args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to start ControlPanel TUI demo", e);
    }
  }
}
