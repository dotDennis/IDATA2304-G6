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

  /**
   * Creates the main view.
   *
   * @param controller the GUI controller
   */
  public MainView(GuiController controller) {
    this.controller = controller;
    root = new BorderPane();

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
    VBox centerBox = new VBox(20);
    centerBox.setAlignment(Pos.CENTER);
    centerBox.setPadding(new

            Insets(50));

    Label placeholder = new Label("GUI Framework");
    placeholder.setFont(Font.font("System", FontWeight.NORMAL, 18));

    Label instructions = new Label("Here: Implement connections");
    instructions.setFont(Font.font("System", FontWeight.NORMAL, 14));
    instructions.setStyle("-fx-text-fill: gray;");

    centerBox.getChildren().

            addAll(placeholder, instructions);
    root.setCenter(centerBox);

    //Status bar
    Label statusBar = new Label("Ready");
    statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5;");
    root.setBottom(statusBar);
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
