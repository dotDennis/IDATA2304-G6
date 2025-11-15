package group6.ui.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the GUI application
 * Coordinates between backend and GUI views
 */
public class GuiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GuiController.class);

  /**
   * Creates a new GUI controller
   */
  public GuiController() {
    LOGGER.info("GuiController initialized");
  }
}
