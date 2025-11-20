package group6.ui.helpers.builders.dialog;

import java.util.Optional;
import java.util.function.Predicate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Utility to build the "Add Control Node" dialog.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public final class ControlNodeDialogBuilder {

  /**
   * Result object representing the dialog entries.
   */
  public record Result(String id, String displayName, long refreshInterval) {
  }

  private ControlNodeDialogBuilder() {
  }

  /**
   * Shows the dialog and returns user input when validation succeeds.
   *
   * @param idExistsPredicate predicate to check for duplicate IDs
   * @return optional result containing the input data
   */
  public static Optional<Result> show(Predicate<String> idExistsPredicate) {
    TextField idField = new TextField();
    TextField nameField = new TextField();
    TextField refreshField = new TextField("1000");
    idField.setPromptText("control-03");
    nameField.setPromptText("Control Node");
    refreshField.setPromptText("1000");

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Add Control Node");
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.addRow(0, new Label("ID:"), idField);
    grid.addRow(1, new Label("Display Name:"), nameField);
    grid.addRow(2, new Label("Refresh (ms):"), refreshField);

    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: #b71c1c;");
    errorLabel.setWrapText(true);

    VBox container = new VBox(10, grid, errorLabel);
    container.setPadding(new Insets(10));
    dialog.getDialogPane().setContent(container);
    dialog.getDialogPane().setPrefWidth(420);
    dialog.setResizable(true);

    final Result[] resultHolder = new Result[1];
    Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
      String id = idField.getText().trim();
      String display = nameField.getText().trim();
      String refreshText = refreshField.getText().trim();
      // error checks
      if (id.isEmpty() || display.isEmpty()) {
        errorLabel.setText("Both ID and display name are required.");
        event.consume();
        return;
      }
      if (idExistsPredicate.test(id)) {
        errorLabel.setText("Control node with ID '" + id + "' already exists.");
        event.consume();
        return;
      }
      long interval;
      try {
        interval = Long.parseLong(refreshText);
      } catch (NumberFormatException ex) {
        errorLabel.setText("Refresh interval must be a number.");
        event.consume();
        return;
      }
      if (interval <= 0) {
        errorLabel.setText("Refresh interval must be positive.");
        event.consume();
        return;
      }

      resultHolder[0] = new Result(id, display, interval);
    });

    dialog.setResultConverter(button -> button);
    dialog.showAndWait();
    return Optional.ofNullable(resultHolder[0]);
  }
}
