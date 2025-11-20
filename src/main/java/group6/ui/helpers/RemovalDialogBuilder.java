package group6.ui.helpers;

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
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public class RemovalDialogBuilder {

  public static <Type> void show(String title,
      List<Type> options,
      Consumer<Type> onRemove) {
    if (options.isEmpty()) {
      return;
    }
    Dialog<Type> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

    ComboBox<Type> comboBox = new ComboBox<>(FXCollections.observableArrayList(options));
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
