package group6.ui.views;

import group6.entity.node.ControlPanel;
import group6.ui.controllers.GuiController;
import group6.ui.helpers.ControlNodeConfig;
import group6.ui.helpers.builders.dialog.ControlNodeDialogBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main view hosting multiple control panel workspaces.
 */
public class MainView {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainView.class);

  private final BorderPane root;
  private final TabPane controlTabs;
  private final Map<String, ControlContext> contexts;
  private final Consumer<ControlNodeConfig> autoSaveHandler;

  /**
   * Constructs the main view and optionally restores from config.
   */
  public MainView(ControlNodeConfig config, Consumer<ControlNodeConfig> autoSaveHandler) {
    this.autoSaveHandler = autoSaveHandler;
    this.root = new BorderPane();
    this.controlTabs = new TabPane();
    this.controlTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    this.contexts = new LinkedHashMap<>();

    setupHeader();
    root.setPadding(new Insets(10));
    root.setCenter(controlTabs);

    if (config != null) {
      for (ControlNodeConfig.Entry entry : config.getEntries()) {
        addControlNode(entry);
      }
    }
  }

  /**
   * Builds the header bar containing the title and add button.
   */
  private void setupHeader() {
    Label title = new Label("🏠 Smart Greenhouse Control Panels");
    title.getStyleClass().add("section-title");

    Button addButton = new Button("Add Control Node");
    addButton.setOnAction(e -> openAddControlDialog());

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox header = new HBox(10, title, spacer, addButton);
    header.setPadding(new Insets(15));
    root.setTop(header);
  }

  /**
   * Opens the add-control dialog and creates the node on success.
   */
  private void openAddControlDialog() {
    ControlNodeDialogBuilder.show(contexts::containsKey)
        .ifPresent(result -> {
          ControlNodeConfig.Entry entry = new ControlNodeConfig.Entry();
          entry.setId(result.id());
          entry.setDisplayName(result.displayName());
          entry.setRefreshInterval(result.refreshInterval());
          addControlNode(entry);
          notifyConfigChanged();
        });
  }

  /**
   * Adds a new tab/workspace for the given entry.
   */
  private void addControlNode(ControlNodeConfig.Entry entry) {
    ControlContext context = createControlContext(entry);
    controlTabs.getTabs().add(context.tab());
    context.workspace().restore(entry);
    contexts.put(entry.getId(), context);
    controlTabs.getSelectionModel().select(context.tab());
  }

  /**
   * Creates the controller/workspace/tab bundle for an entry.
   */
  private ControlContext createControlContext(ControlNodeConfig.Entry entry) {
    if (entry.getDisplayName() == null || entry.getDisplayName().isBlank()) {
      entry.setDisplayName(entry.getId());
    }
    GuiController controller = new GuiController(new ControlPanel(entry.getId()));
    ControlPanelWorkspace workspace = new ControlPanelWorkspace(controller,
        entry.getRefreshInterval(), this::notifyConfigChanged);
    controller.startAutoRefresh(workspace::refreshAllTabs, entry.getRefreshInterval());

    Tab tab = new Tab(entry.getDisplayName());
    tab.setContent(workspace.getRoot());
    tab.setClosable(true);
    tab.setOnCloseRequest(e -> removeControlNode(entry.getId()));
    return new ControlContext(entry, controller, workspace, tab);
  }

  /**
   * Removes a control node tab and shuts it down.
   */
  private void removeControlNode(String id) {
    ControlContext context = contexts.remove(id);
    if (context != null) {
      context.workspace().shutdown();
      controlTabs.getTabs().remove(context.tab());
      notifyConfigChanged();
    }
  }

  /**
   * Gets the root scene for the application.
   * 
   * @return the root scene for the application.
   */
  public Scene getScene() {
    return new Scene(root, 1000, 700);
  }

  /**
   * Shuts down all workspaces and clears state.
   */
  public void shutdown() {
    for (ControlContext context : contexts.values()) {
      context.workspace().shutdown();
    }
    contexts.clear();
  }

  /**
   * Gets whether any control nodes exist.
   * 
   * @return true if at least one control node exists.
   */
  public boolean hasControlNodes() {
    return !contexts.isEmpty();
  }

  /**
   * Serializes current state back to a config object.
   */
  public ControlNodeConfig exportConfig() {
    List<ControlNodeConfig.Entry> entries = new ArrayList<>();
    for (ControlContext context : contexts.values()) {
      ControlNodeConfig.Entry entry = context.workspace()
          .toConfigEntry(context.entry().getId(), context.entry().getDisplayName());
      entries.add(entry);
    }
    return ControlNodeConfig.fromEntries(entries);
  }

  /**
   * Persists config via the auto-save handler, if present.
   */
  private void notifyConfigChanged() {
    if (autoSaveHandler != null) {
      try {
        autoSaveHandler.accept(exportConfig());
      } catch (Exception e) {
        LOGGER.warn("Auto-save handler failed", e);
      }
    }
  }

  private record ControlContext(ControlNodeConfig.Entry entry,
      GuiController controller,
      ControlPanelWorkspace workspace,
      Tab tab) {
  }
}
