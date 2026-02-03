package controller;

import com.valkryst.dds.manager.DDSManager;
import com.valkryst.dds.object.Rule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import model.EditSelectionDialogModel;
import view.EditSelectionDialogView;

public class EditSelectionDialogController extends Stage implements EventHandler {
    /** The database manager to use when interacting with the database. */
    private final DDSManager ddsManager;

    /** The model to use when populating & altering the components of the view with data. */
    @Getter private final EditSelectionDialogModel model;

    /** The view to display to the user. */
    @Getter private final EditSelectionDialogView view;

    /**
     * Construct a new EditSelectionDialogController.
     *
     * @param ddsManager
     *         The database manager to use when interacting with the database.
     */
    public EditSelectionDialogController(final DDSManager ddsManager) {
        this.ddsManager = ddsManager;

        model = new EditSelectionDialogModel(ddsManager);
        view = new EditSelectionDialogView(this, model);

        // Setup Stage:
        final Scene scene = new Scene(view);
        scene.getStylesheets().add("global.css");
        scene.getRoot().getStyleClass().add("main-root");

        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Rule Selection");
        this.setMinHeight(600);
        this.setMinWidth(600);
        this.setScene(scene);
    }

    @Override
    public void handle(Event event) {
        if(event.getSource() instanceof Button) {
            final Button source = (Button)event.getSource();

            if(source.equals(view.getButton_editSelectedRules())) {
                model.getRulesSelectedOnDialogClose().addAll(view.getTableView_rules().getSelectionModel().getSelectedItems());
                this.close();
            }

            if(source.equals(view.getButton_deleteSelectedRules())) {
                int itemsToDelete = view.getTableView_rules().getSelectionModel().getSelectedItems().size();

                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Deleting " + itemsToDelete + " Rules and their Associations from the Dynamic Dialog System.");
                alert.setContentText("This cannot be reversed, are you sure you want to do this?");

                if(alert.showAndWait().get() == ButtonType.OK){
                    view.getTableView_rules().getSelectionModel().getSelectedItems().stream()
                                                                                    .forEach(object -> ddsManager.removeRule((Rule)object));

                    // Delete Records from TableView:
                    final ObservableList<String> tableItems = FXCollections.observableArrayList(view.getTableView_rules().getItems());
                    tableItems.removeAll(view.getTableView_rules().getSelectionModel().getSelectedItems());

                    view.getTableView_rules().setItems(tableItems);
                    view.getTableView_rules().getSelectionModel().clearSelection();
                }
            }
        }
    }
}
