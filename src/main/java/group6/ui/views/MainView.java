package group6.ui.views;


import group6.ui.controllers.GuiController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


/**
 * MainView for the GUI
 * Acts as the root, holding all subviews.
 */
public class MainView {

  private final GuiController controller;
  private final BorderPane root;
  private final Label statusLabel;
  private ConnectionView connectionView;

  /**
   * Creates the main view.
   *
   * @param controller the GUI controller
   */
  public MainView(GuiController controller) {
    this.controller = controller;
    this.root = new BorderPane();
    this.statusLabel = new Label("Ready");

    setupLayout();
  }

  /**
   * Sets up the initial layout with placeholder content for now.
   */
  private void setupLayout() {
    root.setPadding(new Insets(15));

    //Title
    Label title = new Label("Smart Greenhouse Control Panel");
    title.setFont(Font.font("System", FontWeight.BOLD, 24));
    BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
    root.setTop(title);


    //Main page (with placeholder content)
    VBox centerBox = new VBox(15);
    centerBox.setPadding(new Insets(10));

    //Connection section
    connectionView = new ConnectionView(controller);
    connectionView.setStatusLabel(statusLabel);

    //Placeholder for future views
    Label placeholder = new Label("Sensor data and actuator controls" +
            " will appear here after connecting");
    placeholder.setStyle("-fx-text-fill: gray");

    centerBox.getChildren().addAll(connectionView.getView(), placeholder);

    root.setCenter(centerBox);

    //Status bar
    statusLabel.setStyle("-fx-background-color: white; -fx-padding: 5;" );
    root.setBottom(statusLabel);
  }

  /**
   * Gets the scene for this view.
   *
   * @return the scene
   */
  public Scene getScene() {
    return new Scene(root, 600, 500);
  }
}
