package group6.ui.views;

import group6.ui.controllers.GuiController;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for connection controls.
 * Allows user to connect to sensornodes.
 */
public class ConnectionView {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionView.class);

  private final GuiController controller;
  private final HBox view;
  private Label statusLabel;
  private Consumer<String> onNodeConnected;

  /**
   * Creates the connection view.
   *
   * @param controller the GUI controller
   */
  public ConnectionView(GuiController controller) {
    this.controller = controller;
    this.view = createView();
  }

  /**
   * Creates the connection components.
   *
   * @return HBox containing connection controls.
   */
  private HBox createView() {
    HBox box = new HBox(10);
    box.setAlignment(Pos.CENTER_LEFT);

    // Node ID field.
    Label nodeLabel = new Label("Node:");
    TextField nodeIdField = new TextField("sensor-01");
    nodeIdField.setPrefWidth(100);

    // Host field.
    Label hostLabel = new Label("Host:");
    TextField hostField = new TextField("localhost");
    hostField.setPrefWidth(100);

    // Port field
    Label portLabel = new Label("Port:");
    TextField portField = new TextField("12345");
    portField.setPrefWidth(80);

    // Connect button
    Button connectButton = new Button("Connect");
    connectButton.setOnAction(e -> {
      String nodeId = nodeIdField.getText().trim();
      String host = hostField.getText().trim();

      try {
        int port = Integer.parseInt(portField.getText().trim());
        connect(nodeId, host, port);
      } catch (NumberFormatException ex) {
        updateStatus("Invalid port number");
        LOGGER.warn("Invalid port number {}", portField.getText());
      }
    });

    box.getChildren().addAll(nodeLabel, nodeIdField, hostLabel,
        hostField, portLabel, portField, connectButton);
    return box;
  }

  /**
   * Attempts to connect to a sensor node.
   *
   * @param nodeId the ID of the node.
   * @param host   the host address.
   * @param port   the port number.
   */
  private void connect(String nodeId, String host, int port) {
    try {
      controller.connectToNode(nodeId, host, port);
      updateStatus("Connected to " + nodeId + "at " + host + ":" + port);
      LOGGER.info("Connection successful");

      if (onNodeConnected != null) {
        onNodeConnected.accept(nodeId);
      }

    } catch (Exception e) {
      updateStatus("Connection failed: " + e.getMessage());
      LOGGER.error("Connection failed", e);
    }
  }

  /**
   * Updates the status label if available.
   *
   * @param message the status message
   */
  public void updateStatus(String message) {
    if (statusLabel != null) {
      statusLabel.setText(message);
    }
  }

  /**
   * Sets the status label.
   *
   * @param statusLabel the statusLabel
   */
  public void setStatusLabel(Label statusLabel) {
    this.statusLabel = statusLabel;
  }

  /**
   * Sets the callback to invoke when a node is connected.
   *
   * @param callback consumer that accepts the nodeId
   */
  public void setOnNodeConnected(Consumer<String> callback) {
    this.onNodeConnected = callback;
  }

  /**
   * Gets the view.
   *
   * @return the HBox view
   */
  public HBox getView() {
    return view;
  }
}
