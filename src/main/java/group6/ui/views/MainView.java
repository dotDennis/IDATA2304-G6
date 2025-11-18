package group6.ui.views;


import group6.ui.controllers.GuiController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;


/**
 * MainView for the GUI
 * Acts as the root, holding all subviews.
 */
public class MainView {

  private final GuiController controller;
  private final BorderPane root;
  private final Label statusLabel;
  private final TabPane tabPane;
  private final Map<String, NodeTabView> nodeTabs;

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
    this.tabPane = new TabPane();
    this.nodeTabs = new HashMap<>();

    setupLayout();
  }

  /**
   * Sets up the initial layout.
   */
  private void setupLayout() {
    root.setPadding(new Insets(15));

    //Title
    Label title = new Label("🏠 Smart Greenhouse Control Panel");
    title.setFont(Font.font("System", FontWeight.BOLD, 24));
    BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
    root.setTop(title);

    //Main content
    VBox centerBox = new VBox(15);
    centerBox.setPadding(new Insets(10));

    //Connection section
    connectionView = new ConnectionView(controller);
    connectionView.setStatusLabel(statusLabel);

   // Set callback for when a node is connected
    connectionView.setOnNodeConnected(this::addNodeTab);

    //TabPane for multiple nodes
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

    if(nodeTabs.isEmpty()) {
      Label placeholder = new Label("Connect to a sensornode to get started");
      placeholder.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
      placeholder.setPadding(new Insets(20));
      centerBox.getChildren().addAll(connectionView.getView(), placeholder);
    }else {
      centerBox.getChildren().addAll(connectionView.getView(), tabPane);
    }

    root.setCenter(centerBox);


    //Status bar
    statusLabel.setStyle("-fx-background-color: white; -fx-padding: 5;" );
    root.setBottom(statusLabel);
  }

  /**
   * Adds a new tab for a connected ndoe.
   *
   * @param nodeId the ID of the node
   */
  public void addNodeTab(String nodeId) {
    if(nodeTabs.containsKey(nodeId)) {
      statusLabel.setText("Node " + nodeId + " is already connected.");
      return;
    }

    //Create a new tab
    NodeTabView nodeTabView = new NodeTabView(nodeId, controller, () -> removeNodeTab(nodeId));
    nodeTabs.put(nodeId, nodeTabView);
    tabPane.getTabs().add(nodeTabView.getTab());

    //Update center content
    if(nodeTabs.size() == 1) {
      VBox centerBox = new VBox(15);
      centerBox.setPadding(new Insets(10));
      centerBox.getChildren().addAll(connectionView.getView(), tabPane);
      root.setCenter(centerBox);
    }

    //Select new tab
    tabPane.getSelectionModel().select(nodeTabView.getTab());

    statusLabel.setText("Connected to " + nodeId);
  }

  /**
   * Removes a tab for a disconnected node.
   *
   * @param nodeId the ID od the disconnected node
   */
  public void removeNodeTab(String nodeId) {
    NodeTabView nodeTabView = nodeTabs.remove(nodeId);
    if (nodeTabView != null) {
      tabPane.getTabs().remove(nodeTabView.getTab());
      statusLabel.setText("Disconnected from " + nodeId);

      //If no tabs, show placeholder instead
      if(nodeTabs.isEmpty()) {
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(10));
        Label placeholder = new Label("Connect to a sensornode to get started");
        placeholder.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        placeholder.setPadding(new Insets(20));
        centerBox.getChildren().addAll(connectionView.getView(), placeholder);
        root.setCenter(centerBox);
      }
    }
  }

  /**
   * Refreshes all node tabs.
   * Called by auto refresh timer
   */
  public void refreshAllTabs() {
    for (NodeTabView nodeTabView : nodeTabs.values()) {
      nodeTabView.refresh();
    }
  }

  /**
   * Gets the scene for this view.
   *
   * @return the scene
   */
  public Scene getScene() {
    return new Scene(root, 800, 600);
  }
}
