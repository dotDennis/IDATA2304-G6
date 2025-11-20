package group6.ui.helpers.builders.dialog;

import java.util.List;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Utility for building dialog to remove device.
 */
public class RemovalDialogBuilder {

  /**
   * Shows a dialog to remove a device from a list of options.
   * 
   * @param <T>    the device type
   * @param title     the dialog title
   * @param options   the available options
   * @param onRemove  the removal handler
   */
  public static <T> void show(String title,
      List<T> options,
      Consumer<T> onRemove) {
    if (options.isEmpty()) {
      return;
    }
    Dialog<T> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

    ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(options));
    comboBox.getSelectionModel().selectFirst();

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.addRow(0, new Label("Device:"), comboBox);
    dialog.getDialogPane().setContent(grid);
    dialog.getDialogPane().setPrefWidth(360);
    dialog.getDialogPane().setMinWidth(360);
    dialog.setResizable(true);

    dialog.setResultConverter(button -> button == ButtonType.OK ? comboBox.getValue() : null);

    dialog.showAndWait().ifPresent(selection -> {
      if (selection != null) {
        onRemove.accept(selection);
      }
    });
  }
}
