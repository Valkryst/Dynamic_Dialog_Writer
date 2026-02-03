package view;

import com.valkryst.dds.object.Rule;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import model.EditSelectionDialogModel;

public class EditSelectionDialogView extends VBox {
    /** The Button to open the currently selected Rules for editing. */
    @Getter private final Button button_editSelectedRules = new Button("Edit Selected Rule(s)");
    /** The Button to delete the currently selected Rules from the database. */
    @Getter private final Button button_deleteSelectedRules = new Button("Delete Selected Rule(s)");

    /** The Label to warn the user about needing to resave Rules after opening them for editing. */
    private final Label label_warning = new Label("Any Rule opened for editing is entirely removed from the Dynamic Dialog System.\nYou must save it, after opening, for it to be re-added to the system.");

    /** The TextField to enter a search query to search through the TableView of Rules. */
    private final TextField textField_searchQuery = new TextField();

    /** The TableView to display all Rules currently saved to the database. */
    @Getter private final TableView tableView_rules = new TableView();

    /**
     * Construct a new EditSelectionDialogView.
     *
     * @param eventHandler
     *         The EventHandler to use when handling the events of all components
     *         on the view.
     *
     * @param model
     *         The model to use when populating the components of the view with data.
     */
    public EditSelectionDialogView(final EventHandler eventHandler, final EditSelectionDialogModel model) {
        setDefaultComponentTooltips();
        setDefaultComponentEventHandlers(eventHandler);
        setupLabel();
        setupTableView(model);


        this.setSpacing(4);
        this.setPadding(new Insets(4));
        this.getChildren().addAll(setupTopPanel(), setupMiddlePanel(), setupBottomPanel());
    }

    /** Sets all Components to have their default tooltips. */
    public void setDefaultComponentTooltips() {
     }

    /**
     * Sets all Components to use the specified EventHandler as their EventHandler.
     *
     * @param eventHandler
     *         The EventHandler to use.
     */
    private void setDefaultComponentEventHandlers(final EventHandler eventHandler) {
        button_editSelectedRules.setOnAction(eventHandler);
        button_deleteSelectedRules.setOnAction(eventHandler);
    }

    /** Sets up the warning label. */
    private void setupLabel() {
        label_warning.setWrapText(true);
        label_warning.setStyle("-fx-text-fill: RED;-fx-font-size: 14;");
    }

    /**
     * Creates the TableColumns of the TableView and then creates/sets-up
     * the TableView.
     *
     * @param model
     *         The model to use when populating the TableView.
     */
    private void setupTableView(final EditSelectionDialogModel model) {
        // Setup TableView Columns:
        final TableColumn column_event = new TableColumn("Event");
        column_event.setCellValueFactory(new PropertyValueFactory("event"));

        final TableColumn column_description = new TableColumn("Rule Description");
        column_description.setCellValueFactory(new PropertyValueFactory("description"));


        // Setup TableViews:
        tableView_rules.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView_rules.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView_rules.getColumns().setAll(column_event, column_description);


        // Setup Searching with the Search Query Text Field:
        final FilteredList<Rule> filteredList = new FilteredList<>(model.getRules(), p -> true);

        textField_searchQuery.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(ruleModel -> newValue == null || newValue.isEmpty() || ruleModel.getDescription().toLowerCase().contains(newValue.toLowerCase()));
        });

        final SortedList<Rule> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView_rules.comparatorProperty());
        tableView_rules.setItems(sortedList);
    }

    /**
     * Sets up the top panel of the view.
     *
     * @return
     *         The top panel of the view.
     */
    private HBox setupTopPanel() {
        final HBox panel = new HBox(10);
        HBox.setHgrow(panel, Priority.ALWAYS);
        HBox.setHgrow(textField_searchQuery, Priority.ALWAYS);
        panel.getChildren().addAll(textField_searchQuery);

        return panel;
    }

    /**
     * Sets up the middle panel of the view.
     *
     * @return
     *         The middle panel of the view.
     */
    private HBox setupMiddlePanel() {
        final HBox panel = new HBox(10);
        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        HBox.setHgrow(tableView_rules, Priority.ALWAYS);
        VBox.setVgrow(tableView_rules, Priority.ALWAYS);
        panel.getChildren().add(tableView_rules);

        return panel;
    }

    /**
     * Sets up the bottom panel of the view.
     *
     * @return
     *         The bottom panel of the view.
     */
    private VBox setupBottomPanel() {
        final HBox panel_top = new HBox(10);
        panel_top.setAlignment(Pos.CENTER);
        button_editSelectedRules.setAlignment(Pos.CENTER);
        HBox.setHgrow(button_editSelectedRules, Priority.NEVER);
        HBox.setHgrow(button_deleteSelectedRules, Priority.NEVER);
        panel_top.getChildren().addAll(button_editSelectedRules, button_deleteSelectedRules);

        final VBox panel = new VBox(4);
        panel.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(panel_top, label_warning);

        return panel;
    }
}
