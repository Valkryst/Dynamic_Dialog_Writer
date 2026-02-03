package view;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class MainScreenView extends VBox {
    /** The Button to open a new blank RuleScreenView tab. */
    @Getter private final Button button_newRule = new Button("New Rule", new ImageView(new Image(this.getClass().getResourceAsStream("/icons/NewDocument.png"), 20, 20, false, false)));
    /** The button to save the currently selected RuleScreenView tab. */
    @Getter private final Button button_saveRule = new Button("Save Selected Rule", new ImageView(new Image(this.getClass().getResourceAsStream("/icons/SaveDocument.png"), 20, 20, false, false)));
    /** The Button to open the EditSelectionDialog. */
    @Getter private final Button button_editRule = new Button("Edit Exiting Rule(s)", new ImageView(new Image(this.getClass().getResourceAsStream("/icons/EditDocument.png"), 20, 20, false, false)));
    /** The Button to load a new database. */
    @Getter private final Button button_loadDatabase = new Button("Load Database", new ImageView(new Image(this.getClass().getResourceAsStream("/icons/OpenDatabase.png"), 25, 20, false, false)));
    /** The Button to save the currently open database. */
    @Getter private final Button button_saveDatabase = new Button("Save Database", new ImageView(new Image(this.getClass().getResourceAsStream("/icons/SaveDatabase.png"), 25, 20, false, false)));

    /** The TabPane containing all of the RuleScreenViews. */
    @Getter private final TabPane tabPane = new TabPane();

    /**
     * Construct a new MainScreenView.
     *
     * @param eventHandler
     *         The EventHandler to use when handling the events of all components
     *         on the view.
     */
    public MainScreenView(final EventHandler eventHandler) {
        setDefaultComponentTooltips();
        setDefaultComponentEventHandlers(eventHandler);
        disableButtons();


        this.getChildren().addAll(setupTopPanel(), tabPane);
    }

    /** Disables all Buttons. */
    public void disableButtons() {
        button_newRule.setDisable(true);
        button_saveRule.setDisable(true);
        button_editRule.setDisable(true);
    }

    /** Enables all Buttons. */
    public void enableButtons() {
        button_newRule.setDisable(false);
        button_saveRule.setDisable(false);
        button_editRule.setDisable(false);
    }

    /** Sets all Components to have their default tooltips. */
    private void setDefaultComponentTooltips() {
        button_newRule.setTooltip(new Tooltip("Opens a blank Rule tab where a new Rule can be created."));
        button_saveRule.setTooltip(new Tooltip("Saves the Rule on the currently open tab to the database if all information is acceptable."));
        button_editRule.setTooltip(new Tooltip("Opens a tab where you can search from all existing Rules and select one, or more, to open and edit."));
        button_loadDatabase.setTooltip(new Tooltip("Opens a file selection window where you can select a new database to edit."));
        button_saveDatabase.setTooltip(new Tooltip("Saves the currently open database.\n\nPlease note that without saving the database in this way,\nall edits you have made since the last save will be erased\nwhen closing the program."));
    }

    /**
     * Sets all Components to use the specified EventHandler as their EventHandler.
     *
     * @param eventHandler
     *         The EventHandler to use.
     */
    private void setDefaultComponentEventHandlers(final EventHandler eventHandler) {
        button_newRule.setOnAction(eventHandler);
        button_saveRule.setOnAction(eventHandler);
        button_editRule.setOnAction(eventHandler);
        button_loadDatabase.setOnAction(eventHandler);
        button_saveDatabase.setOnAction(eventHandler);
    }

    /**
     * Sets up the top panel of the view.
     *
     * @return
     *         The top panel of the view.
     */
    private HBox setupTopPanel() {
        final HBox panel = new HBox(10);
        panel.getStylesheets().add("button_flat.css");
        panel.getChildren().addAll(button_newRule, button_saveRule, button_editRule, new Separator(Orientation.VERTICAL), button_loadDatabase, button_saveDatabase);

        return panel;
    }
}
