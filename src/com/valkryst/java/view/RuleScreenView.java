package view;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import model.RuleScreenModel;

public class RuleScreenView extends Tab {
    /** The Button to add a new condition to the table of conditions. */
    @Getter private final Button button_addCondition = new Button("Add Condition");
    /** The Button to add a new response to the table of responses. */
    @Getter private final Button button_addResponse = new Button("Add Response");
    /** The Button to remove all conditions currently selected from the table of conditions. */
    @Getter private final Button button_removeConditions= new Button("Delete Selected Condition(s)");
    /** The Button to remove all responses currently selected from the table of responses. */
    @Getter private final Button button_removeResponses = new Button("Delete Selected Response(s)");

    /** The ComboBox to select an Event with. */
    @Getter private final ComboBox<String> comboBox_events = new ComboBox<>();
    /** The ComboBox to select a Context with. */
    @Getter private final ComboBox<String> comboBox_contexts = new ComboBox<>();
    /** The ComboBox to select a Comparison Type with. */
    @Getter private final ComboBox<String> comboBox_comparisonTypes = new ComboBox<>();
    /** The ComboBox to select a Response Type with. */
    @Getter private final ComboBox<String> comboBox_responseTypes = new ComboBox<>();

    /** The TableView to display all added conditions. */
    @Getter private final TableView tableView_conditions = new TableView();
    /** The TableView to display all added responses. */
    @Getter private final TableView tableView_responses = new TableView();

    /** The TextField for the description of the Rule. */
    @Getter private final TextField textField_description = new TextField();
    /** The TextField for the value to compare against the selected Context of the comboBox_contexts. */
    @Getter private final TextField textField_contextValue = new TextField();
    /** The TextField for the value to use with the selected Response Type of the comboBox_responsetypes. */
    @Getter private final TextField textField_responseValue = new TextField();


    /**
     * Construct a new RuleScreenView.
     *
     * @param tabID
     *         The ID of the tab to be used in the Tab's name.
     *
     * @param eventHandler
     *         The EventHandler to use when handling the events of all components
     *         on the view.
     *
     * @param model
     *         The model to use when populating the components of the view with data.
     */
    public RuleScreenView(final int tabID, final EventHandler eventHandler, final RuleScreenModel model) {
        setDefaultComponentTooltips();
        setDefaultComponentEventHandlers(eventHandler);
        setupComboBoxes(model);
        setupTableViews(model);
        setDefaultFieldPrompts();


        final VBox panel_main = new VBox(4);
        panel_main.getChildren().addAll(setupEventTitledPane(), setupConditionsTitledPane(), setupResponsesTitledPane());

        this.setContent(panel_main);
        this.setText("New Rule #" + tabID);
    }

    /** Sets all Components to have their default tooltips. */
    public void setDefaultComponentTooltips() {
        textField_description.setTooltip(new Tooltip("The description of the Rule. This is used when searching\nfor Rules to edit, so it is highly recommended that this\nbe filled-in."));
        textField_contextValue.setTooltip(new Tooltip("The value being compared against.\n\nEx:\n\tPlayer HP = Value"));
        textField_responseValue.setTooltip(new Tooltip("The value for the Response to use. This is only applicable with certain Responses.\n\nEx:\n\tResponse - Increase Score\n\tValue - 10\n\n\tSo, the score is increased by 10 points."));
    }

    /**
     * Sets all Components to use the specified EventHandler as their EventHandler.
     *
     * @param eventHandler
     *         The EventHandler to use.
     */
    private void setDefaultComponentEventHandlers(final EventHandler eventHandler) {
        button_addCondition.setOnAction(eventHandler);
        button_addResponse.setOnAction(eventHandler);
        button_removeConditions.setOnAction(eventHandler);
        button_removeResponses.setOnAction(eventHandler);
    }

    /**
     * Populates all of the ComboBoxes and sets their initial selection values.
     *
     * @param model
     *         The model to use when populating the ComboBoxes.
     */
    private void setupComboBoxes(final RuleScreenModel model) {
        comboBox_events.getItems().addAll(model.getEvents());
        comboBox_comparisonTypes.getItems().addAll(model.getComparisonTypes());
        comboBox_contexts.getItems().addAll(model.getContexts());
        comboBox_responseTypes.getItems().addAll(model.getResponseTypes());


        comboBox_events.getSelectionModel().select(0);
        comboBox_comparisonTypes.getSelectionModel().select(0);
        comboBox_contexts.getSelectionModel().select(0);
        comboBox_responseTypes.getSelectionModel().select(0);
    }

    /**
     * Creates the TableColumns of the TableViews and then creates/sets-up
     * the TableViews.
     *
     * @param model
     *         The model to use when populating the TableViews.
     */
    private void setupTableViews(final RuleScreenModel model) {
        // Setup TableView Columns:
        final TableColumn column_context = new TableColumn("Context");
        column_context.setCellValueFactory(new PropertyValueFactory("context"));

        final TableColumn column_comparisonType = new TableColumn("Comparison Type");
        column_comparisonType.setCellValueFactory(new PropertyValueFactory("comparisonType"));

        final TableColumn column_valueToCompareTo = new TableColumn("Value to Compare To");
        column_valueToCompareTo.setCellValueFactory(new PropertyValueFactory("valueToCompareTo"));



        final TableColumn column_responseType = new TableColumn("Response Type");
        column_responseType.setCellValueFactory(new PropertyValueFactory("responseType"));

        final TableColumn column_responseValue = new TableColumn("Response Value");
        column_responseValue.setCellValueFactory(new PropertyValueFactory("responseValue"));


        // Setup TableViews:
        tableView_conditions.setEditable(false);
        tableView_conditions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView_conditions.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView_conditions.setItems(model.getConditions());
        tableView_conditions.getColumns().setAll(column_context, column_comparisonType, column_valueToCompareTo);

        tableView_responses.setEditable(false);
        tableView_responses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView_responses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView_responses.setItems(model.getResponses());
        tableView_responses.getColumns().addAll(column_responseType, column_responseValue);
    }

    /** Sets the default prompt text for all fields. */
    private void setDefaultFieldPrompts() {
        textField_description.setPromptText("Description of the Rule");
        textField_contextValue.setPromptText("Value to Compare To");
        textField_responseValue.setPromptText("Value");
    }

    /**
     * Sets up the event titled panel of the view.
     *
     * @return
     *         The event titled panel.
     */
    private TitledPane setupEventTitledPane() {
        final HBox panel = new HBox(10);
        HBox.setHgrow(textField_description, Priority.ALWAYS);
        panel.getChildren().addAll(comboBox_events, textField_description);

        final TitledPane titledPane = new TitledPane("Event & Description", panel);
        VBox.setVgrow(titledPane, Priority.ALWAYS);

        return titledPane;
    }

    /**
     * Sets up the conditions titled panel of the view.
     *
     * @return
     *         The conditions titled panel.
     */
    private TitledPane setupConditionsTitledPane() {
        final VBox panel_top = new VBox(4);
        HBox.setHgrow(panel_top, Priority.ALWAYS);
        VBox.setVgrow(panel_top, Priority.ALWAYS);
        VBox.setVgrow(tableView_conditions, Priority.ALWAYS);
        panel_top.getChildren().add(tableView_conditions);



        final HBox panel_bottom = new HBox(10);
        HBox.setHgrow(panel_bottom, Priority.ALWAYS);
        HBox.setHgrow(comboBox_contexts, Priority.ALWAYS);
        HBox.setHgrow(comboBox_comparisonTypes, Priority.ALWAYS);
        panel_bottom.getChildren().addAll(comboBox_contexts, comboBox_comparisonTypes, textField_contextValue, button_addCondition, button_removeConditions);


        final VBox panel = new VBox(4);
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.getChildren().addAll(panel_top, panel_bottom);


        final TitledPane titledPane = new TitledPane("Conditions", panel);
        VBox.setVgrow(titledPane, Priority.ALWAYS);

        return titledPane;
    }

    /**
     * Sets up the responses titled panel of the view.
     *
     * @return
     *         The responses titled panel.
     */
    private TitledPane setupResponsesTitledPane() {
        final VBox panel_top = new VBox(4);
        HBox.setHgrow(panel_top, Priority.ALWAYS);
        VBox.setVgrow(panel_top, Priority.ALWAYS);
        VBox.setVgrow(tableView_responses, Priority.ALWAYS);
        panel_top.getChildren().add(tableView_responses);


        final HBox panel_bottom = new HBox(10);
        HBox.setHgrow(panel_bottom, Priority.ALWAYS);
        HBox.setHgrow(comboBox_responseTypes, Priority.ALWAYS);
        panel_bottom.getChildren().addAll(comboBox_responseTypes, textField_responseValue, button_addResponse, button_removeResponses);


        final VBox panel = new VBox(4);
        panel.getChildren().addAll(panel_top, panel_bottom);

        final TitledPane titledPane = new TitledPane("Responses", panel);
        VBox.setVgrow(titledPane, Priority.ALWAYS);

        return titledPane;
    }
}
