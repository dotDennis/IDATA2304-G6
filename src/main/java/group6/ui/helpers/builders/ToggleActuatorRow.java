package group6.ui.helpers.builders;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Generic toggle row component for actuator-like devices.
 * 
 * @author Dennis
 * @since 0.2.0
 */
public class ToggleActuatorRow {

  private final HBox root;
  private final Label nameLabel;
  private final Label statusLabel;
  private final RadioButton onButton;
  private final RadioButton offButton;
  private boolean suppressEvents;
  private Boolean pendingExpectedState;

  /**
   * Creates a new toggle device row.
   * 
   * @param initialState    the initial state of the device
   * @param commandCallback the callback to call when user toggles
   */
  public ToggleActuatorRow(boolean initialState, Consumer<Boolean> commandCallback) {
    this.root = new HBox(10);
    this.root.setAlignment(Pos.CENTER_LEFT);

    this.nameLabel = new Label();
    nameLabel.setWrapText(true);
    nameLabel.setPrefWidth(230);
    nameLabel.setMinWidth(200);
    nameLabel.setFont(Font.font(14));

    this.statusLabel = new Label();
    statusLabel.setPrefWidth(90);
    statusLabel.setStyle("-fx-text-fill: -fx-text-base-color;");

    ToggleGroup group = new ToggleGroup();
    this.onButton = new RadioButton("ON");
    this.offButton = new RadioButton("OFF");
    onButton.setToggleGroup(group);
    offButton.setToggleGroup(group);

    this.onButton.setOnAction(e -> {
      if (!suppressEvents && onButton.isSelected()) {
        commandCallback.accept(true);
      }
    });
    this.offButton.setOnAction(e -> {
      if (!suppressEvents && offButton.isSelected()) {
        commandCallback.accept(false);
      }
    });

    HBox toggleBox = new HBox(10, onButton, offButton);
    toggleBox.setAlignment(Pos.CENTER_LEFT);
    root.getChildren().addAll(nameLabel, toggleBox, statusLabel);

    applyServerState(initialState);
  }

  /**
   * Gets the root HBox of this row.
   * 
   * @return the root HBox
   */
  public HBox getRoot() {
    return root;
  }

  /**
   * Sets the display text of the device.
   * 
   * @param text the display text
   */
  public void setDisplayText(String text) {
    nameLabel.setText(text);
  }

  /**
   * Applies the server state to the toggle buttons.
   * 
   * @param serverState the state from the server
   */
  public void applyServerState(boolean serverState) {
    suppressEvents = true;
    if (serverState) {
      onButton.setSelected(true);
    } else {
      offButton.setSelected(true);
    }
    suppressEvents = false;
    if (pendingExpectedState != null && pendingExpectedState == serverState) {
      pendingExpectedState = null;
    }
    updatePendingVisual();
  }

  /**
   * Marks the row as pending with the expected state.
   * Expected state is whatever the user just requested.
   * 
   * @param expectedState the expected state after the command completes
   */
  public void markPending(boolean expectedState) {
    pendingExpectedState = expectedState;
    updatePendingVisual();
  }

  /**
   * Clears any pending state, visually aswell.
   */
  public void clearPending() {
    pendingExpectedState = null;
    updatePendingVisual();
  }

  private void updatePendingVisual() {
    boolean pending = pendingExpectedState != null;
    onButton.setDisable(pending);
    offButton.setDisable(pending);
    statusLabel.setText(pending ? "Updating..." : "");
    statusLabel.setStyle(pending ? "-fx-text-fill: #c08400;" : "-fx-text-fill: -fx-text-base-color;");
  }
}
