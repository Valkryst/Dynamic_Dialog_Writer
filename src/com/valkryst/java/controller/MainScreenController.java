package controller;

import com.valkryst.dds.manager.DDSManager;
import com.valkryst.dds.object.*;
import eu.hansolo.enzo.notification.Notification;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import model.DataModels.ConditionsModel;
import model.DataModels.ResponsesModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.MainScreenView;
import view.RuleScreenView;

import java.io.*;
import java.util.ArrayList;

public class MainScreenController implements EventHandler {
    // todo JavaDoc
    private final Stage primaryStage;

    // todo JavaDoc
    private final ArrayList<RuleScreenController> tabControllers = new ArrayList<>(10);

    private File openedDatabaseFile;

    /** The database manager to use when interacting with the database. */
    private DDSManager ddsManager;

    /** The view to display to the user. */
    @Getter private final MainScreenView view = new MainScreenView(this);

    public MainScreenController(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void handle(Event event) {
        if(event.getSource() instanceof Button) {
            final Button source = (Button)event.getSource();

            if(source.equals(view.getButton_newRule())) {
                addBlankRuleTab();
            }


            if(source.equals(view.getButton_saveRule())) {
                final RuleScreenView ruleScreenView = (RuleScreenView) view.getTabPane().getSelectionModel().getSelectedItem();

                final String ruleEvent = ruleScreenView.getComboBox_events().getSelectionModel().getSelectedItem();
                final Rule rule = new Rule(ruleEvent, ruleScreenView.getTextField_description().getText(), 0);

                saveRule(ruleScreenView);

                ddsManager.removeRule(rule);

                Notification.Notifier.INSTANCE.notifySuccess("Success", "Rule Saved");
            }


            if(source.equals(view.getButton_editRule())) {
                final EditSelectionDialogController controller = new EditSelectionDialogController(ddsManager);
                controller.show();

                // When the dialog is closed, the following event is triggered:
                controller.setOnHiding(e -> {
                    // For all selected items which are instances of Rule, create a new RuleScreenView Tab
                    // and populate it with retrieved Rule data.
                    controller.getModel().getRulesSelectedOnDialogClose().stream()
                                                                                        .forEachOrdered(this::loadRule);

                    controller.getModel().getRulesSelectedOnDialogClose().stream().forEach(rule -> ddsManager.removeRule((Rule) rule));
                });
            }


            if(source.equals(view.getButton_loadDatabase())) {
                boolean userAcceptedConfirmation = true;

                if (view.getTabPane().getTabs().size() > 0) {
                    final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Unsaved Work");
                    alert.setContentText("You currently have tabs open. Please ensure that all of your Rules have been " +
                            "saved and that you have saved the database itself.\n\nDo you still wish to erase " +
                            "all unsaved work, close the current database, and open a new database?");
                    alert.showAndWait();

                    userAcceptedConfirmation = alert.getResult() != ButtonType.CANCEL;
                }

                if (userAcceptedConfirmation) {
                    // Open file chooser:
                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                    fileChooser.setTitle("Database Selection");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("SER", "*.ser")
                    );


                    // Setup new DataManager:
                    File file = null;
                    try {
                        file = fileChooser.showOpenDialog(primaryStage);

                        if (file != null) {
                            openedDatabaseFile = file;
                            ddsManager = (DDSManager) new ObjectInputStream(new FileInputStream(file)).readObject();

                            view.enableButtons();

                            // Close all opened tabs:
                            view.getTabPane().getTabs().removeAll(view.getTabPane().getTabs());

                            Notification.Notifier.INSTANCE.notifySuccess("Success", "Database Loaded");
                        }
                    } catch (final ClassNotFoundException e) {
                        final Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Database Deserialization Error");
                        alert.setHeaderText("There was an error deserializing the specified file. See error log for more detail.");
                        alert.showAndWait();

                        final Logger logger = LogManager.getLogger();
                        logger.error("The file " + file.getAbsoluteFile() +
                                     " is either not compatible with the current Dynamic Dialogue " +
                                     "System or it's not a serialized Dynamic Dialogue file. See " +
                                     "exception message for more detail.");

                        // Disable buttons:
                        view.disableButtons();
                    } catch (final IOException e) {
                        final Logger logger = LogManager.getLogger();
                        logger.error(e);
                    }
                }
            }


            if(source.equals(view.getButton_saveDatabase())) {
                try {
                    if(openedDatabaseFile != null) {
                        final FileOutputStream fos = new FileOutputStream(openedDatabaseFile);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(ddsManager);

                        oos.close();
                    }
                } catch(final Exception err) {
                    err.printStackTrace();
                }

                Notification.Notifier.INSTANCE.notifySuccess("Success", "Database Saved");
            }
        }
    }

    /**
     * Uses the data from the specified RuleScreenView to construct a Rule, with all
     * Conditions & Responses, and adds it to the Dynamic Dialog System.
     *
     * @param ruleScreenView
     *         The RuleScreenView to use when constructing the Rule and it's Conditions & Responses.
     */
    public void saveRule(final RuleScreenView ruleScreenView) {
        // Add the rule to the Dynamic Dialog System:
        final String ruleEvent = ruleScreenView.getComboBox_events().getSelectionModel().getSelectedItem();
        final Rule rule = new Rule(ruleEvent, ruleScreenView.getTextField_description().getText(), 0);
        ddsManager.addRule(rule);
        ddsManager.addRuleEventAssociation(ruleEvent, rule);


        // Add all conditions to the Dynamic Dialog System:
        ruleScreenView.getTableView_conditions().getItems().parallelStream()
                                                           .forEach(object -> {
                                                               final ConditionsModel conditionsModel = (ConditionsModel) object;

                                                               final Context context = ddsManager.getHashMap_contexts().get(removeDataTypeFromContextName(conditionsModel.getContext().get()));
                                                               final ComparisonType comparisonType = ComparisonType.getComparisonTypeByName(conditionsModel.getComparisonType().get());
                                                               final ValueType valueType = ValueType.getValueTypeByName(conditionsModel.getContext().get());

                                                               final Criterion criterion = new Criterion(context, comparisonType, valueType, conditionsModel.getValueToCompareTo(), 1.0, false); // todo Replace the 1.0 with an actual user-defined weight
                                                               ddsManager.addCriterion(criterion);
                                                               ddsManager.addRuleCriterionAssociation(rule, criterion);
                                                           });



        // Add all responses to the Dynamic Dialog System:
        ruleScreenView.getTableView_responses().getItems().parallelStream()
                                                          .forEach(object -> {
                                                              final ResponsesModel responsesModel = (ResponsesModel) object;

                                                              final Response response = new Response(responsesModel.getResponseType().get(), responsesModel.getResponseValue().get());
                                                              ddsManager.addResponse(response);
                                                              ddsManager.addRuleResponseAssociation(rule, response);
                                                          });
    }

    /**
     * Loads the specified Rule, with it's Conditions & Responses, from the Dynamic Dialog System
     * into a new tab.
     *
     * @param rule
     *         The Rule to load into a new tab.
     */
    public void loadRule(final Rule rule) {
        // Create new Tab and populate it with the retrieved data:
        final RuleScreenController newRSController = addBlankRuleTab();

        // Populate Conditions TableView
        ddsManager.getAssociatedCriterions(rule).parallelStream()
                                                 .forEach(criterion -> {
                                                     final String context = criterion.getContext().getName() + " - (" + criterion.getContext().getValueType() + ")";
                                                     final ConditionsModel model = new ConditionsModel(context, criterion.getComparisonType().name(), criterion.getContext().getValue());
                                                     newRSController.getModel().getConditions().add(model);
                                                 });

        // Populate Responses TableView
        ddsManager.getAssociatedResponses(rule).parallelStream()
                                                .forEach(response -> {
                                                    final ResponsesModel model = new ResponsesModel(response.getResponseType(), response.getValue());
                                                    newRSController.getModel().getResponses().add(model);
                                                });

        newRSController.getView().getComboBox_events().getSelectionModel().select(rule.getEvent());
        newRSController.getView().getTextField_description().setText(rule.getDescription());

        newRSController.getView().setText(newRSController.getView().getText().replace("New Rule", "Rule"));

    }

    // todo JavaDoc
    private String removeDataTypeFromContextName(String contextName) {
        contextName = contextName.replace(" - (BYTE)", "");
        contextName = contextName.replace(" - (SHORT)", "");
        contextName = contextName.replace(" - (INTEGER)", "");
        contextName = contextName.replace(" - (LONG)", "");
        contextName = contextName.replace(" - (FLOAT)", "");
        contextName = contextName.replace(" - (DOUBLE)", "");
        contextName = contextName.replace(" - (BOOLEAN)", "");
        contextName = contextName.replace(" - (STRING)", "");
        contextName = contextName.replace(" - (MP3)", "");
        contextName = contextName.replace(" - (OGG)", "");
        contextName = contextName.replace(" - (WAV)", "");
        return contextName;
    }

    // todo JavaDoc
    public RuleScreenController addBlankRuleTab() {
        final RuleScreenController controller = new RuleScreenController(tabControllers.size() + 1, ddsManager);

        // Add the new controller to the tab pane.
        tabControllers.add(controller);
        view.getTabPane().getTabs().add(controller.getView());

        return controller;
    }
}
