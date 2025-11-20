package group6.ui.helpers.builders.dialog;

import java.util.Optional;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Helper for building dialogs to add new devices with common validation.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public class DeviceDialogBuilder {

  public static <Type extends Enum<Type>> void showDeviceDialog(String title,
      Type[] types,
      Function<Type, String> idSupplier,
      DeviceDialogHandler<Type> handler) {
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

    ComboBox<Type> typeCombo = new ComboBox<>(FXCollections.observableArrayList(types));
    typeCombo.getSelectionModel().selectFirst();

    TextField idField = new TextField();
    if (typeCombo.getValue() != null) {
      idField.setText(idSupplier.apply(typeCombo.getValue()));
    }

    typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        idField.setText(idSupplier.apply(newVal));
      }
    });

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.addRow(0, new Label("Type:"), typeCombo);
    grid.addRow(1, new Label("Device ID:"), idField);
    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: #b71c1c;");
    errorLabel.setWrapText(true);

    VBox container = new VBox(10, grid, errorLabel);
    dialog.getDialogPane().setContent(container);
    dialog.getDialogPane().setPrefWidth(400);
    dialog.getDialogPane().setMinWidth(400);
    dialog.setResizable(true);

    Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
      Type type = typeCombo.getValue();
      String deviceId = idField.getText().trim();
      if (type == null || deviceId.isEmpty()) {
        errorLabel.setText("Type and Device ID are required.");
        event.consume();
        return;
      }
      Optional<String> error = handler.handle(type, deviceId);
      if (error.isPresent()) {
        errorLabel.setText(error.get());
        event.consume();
      }
    });

    dialog.showAndWait();
  }

  public static <Type extends Enum<Type>> void showDeviceDialogWithInterval(String title,
      Type[] types,
      Function<Type, String> idSupplier,
      DeviceDialogWithIntervalHandler<Type> handler,
      long defaultIntervalMs) {
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

    ComboBox<Type> typeCombo = new ComboBox<>(FXCollections.observableArrayList(types));
    typeCombo.getSelectionModel().selectFirst();

    TextField idField = new TextField();
    if (typeCombo.getValue() != null) {
      idField.setText(idSupplier.apply(typeCombo.getValue()));
    }

    typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        idField.setText(idSupplier.apply(newVal));
      }
    });

    TextField intervalField = new TextField(Long.toString(defaultIntervalMs));
    intervalField.setPromptText("5000");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.addRow(0, new Label("Type:"), typeCombo);
    grid.addRow(1, new Label("Device ID:"), idField);
    grid.addRow(2, new Label("Update interval (ms):"), intervalField);
    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: #b71c1c;");
    errorLabel.setWrapText(true);

    VBox container = new VBox(10, grid, errorLabel);
    dialog.getDialogPane().setContent(container);
    dialog.getDialogPane().setPrefWidth(420);
    dialog.getDialogPane().setMinWidth(420);
    dialog.setResizable(true);

    Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
      Type type = typeCombo.getValue();
      String deviceId = idField.getText().trim();
      if (type == null || deviceId.isEmpty()) {
        errorLabel.setText("Type and Device ID are required.");
        event.consume();
        return;
      }
      long interval;
      try {
        interval = Long.parseLong(intervalField.getText().trim());
      } catch (NumberFormatException ex) {
        errorLabel.setText("Update interval must be a number.");
        event.consume();
        return;
      }
      if (interval <= 0) {
        errorLabel.setText("Update interval must be positive.");
        event.consume();
        return;
      }
      Optional<String> error = handler.handle(type, deviceId, interval);
      if (error.isPresent()) {
        errorLabel.setText(error.get());
        event.consume();
      }
    });

    dialog.showAndWait();
  }

  public interface DeviceDialogHandler<Type extends Enum<Type>> {
    Optional<String> handle(Type type, String deviceId);
  }

  public interface DeviceDialogWithIntervalHandler<Type extends Enum<Type>> {
    Optional<String> handle(Type type, String deviceId, long intervalMs);
  }
}
