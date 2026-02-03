package controller;

import com.valkryst.dds.manager.DDSManager;
import com.valkryst.dds.object.Context;
import eu.hansolo.enzo.notification.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import lombok.Getter;
import model.DataModels.ConditionsModel;
import model.DataModels.ResponsesModel;
import model.RuleScreenModel;
import view.RuleScreenView;

public class RuleScreenController implements EventHandler {
    private final DDSManager ddsManager;

    @Getter private final RuleScreenModel model;

    @Getter private final RuleScreenView view;

    public RuleScreenController(final int tabID, final DDSManager ddsManager) {
        this.ddsManager = ddsManager;

        model = new RuleScreenModel(ddsManager);
        view = new RuleScreenView(tabID, this, model);
    }

    @Override
    public void handle(Event event) {
        if(event.getSource() instanceof Button) {
            final Button source = (Button)event.getSource();

            if(source.equals(view.getButton_addCondition())) {
                view.setDefaultComponentTooltips();

                if(isConditionValid()) {
                    final String context = model.getContexts().get(view.getComboBox_contexts().getSelectionModel().getSelectedIndex());
                    final String comparisonType = model.getComparisonTypes().get(view.getComboBox_comparisonTypes().getSelectionModel().getSelectedIndex());
                    final String valueToCompareTo = view.getTextField_contextValue().getText();
                    final ConditionsModel conditionsModel = new ConditionsModel(context, comparisonType, valueToCompareTo);

                    // If the response is not a duplicate of an existing condition, then add it.
                    boolean foundDuplicate = model.getConditions().parallelStream()
                            .anyMatch(cm -> cm.equals(conditionsModel));

                    if (!foundDuplicate) {
                        model.getConditions().add(conditionsModel);
                    }

                    // Clear value field:
                    view.getTextField_contextValue().clear();
                } else {
                    // Alter Tooltip Text:
                    view.getTextField_contextValue().getStylesheets().remove("global.css");
                    view.getTextField_contextValue().getStylesheets().add("field_error.css");

                    final String errorText = "The value specified in this field could not be parsed to the " +
                                             "value type of the specified context '" +
                                             view.getComboBox_contexts().getSelectionModel().getSelectedItem() + "'.";
                    view.getTextField_contextValue().setTooltip(new Tooltip(errorText));

                    // Notify User:
                    Notification.Notifier.INSTANCE.notifyError("Error", "Mouse-over the condition value for more\ninformation.");
                }
            }


            if(source.equals(view.getButton_addResponse())) {
                view.setDefaultComponentTooltips();

                final String responseType = model.getResponseTypes().get(view.getComboBox_responseTypes().getSelectionModel().getSelectedIndex());
                final String responseValue = view.getTextField_responseValue().getText();
                final ResponsesModel responsesModel = new ResponsesModel(responseType, responseValue);

                // If the response is not a duplicate of an existing response, then add it.
                boolean foundDuplicate = model.getResponses().parallelStream()
                                                                            .anyMatch(rm -> rm.equals(responsesModel));

                if(! foundDuplicate) {
                    model.getResponses().add(responsesModel);
                }

                // Clear value field:
                view.getTextField_responseValue().clear();
            }


            if(source.equals(view.getButton_removeConditions())) {
                final ObservableList<String> copy = FXCollections.observableArrayList(view.getTableView_conditions().getSelectionModel().getSelectedItems());
                view.getTableView_conditions().getItems().removeAll(copy);

                view.getTableView_conditions().getSelectionModel().clearSelection();
            }

            if(source.equals(view.getButton_removeResponses())) {
                final ObservableList<String> copy = FXCollections.observableArrayList(view.getTableView_responses().getSelectionModel().getSelectedItems());
                view.getTableView_responses().getItems().removeAll(copy);

                view.getTableView_responses().getSelectionModel().clearSelection();
            }
        }
    }

    /**
     * Determines if the entered condition value can be parsed to the ValueType of the
     * selected Context.
     *
     * If the ValueType is anything other than a Byte, Short, Integer, Long, Float, Double,
     * and Boolean, then true is returned.
     *
     * @return
     *         Whether or not the entered condition value can be parsed to the Valuetype of
     *         the selected Context.
     */
    private boolean isConditionValid() {
        final String selectedContextName = view.getComboBox_contexts()
                                               .getSelectionModel()
                                               .getSelectedItem();

        final Context context = ddsManager.getHashMap_contexts()
                                          .get(selectedContextName);

        final String data = view.getTextField_contextValue().getText();

        switch(context.getValueType()) {
            case BYTE: {
                try {
                    Byte.valueOf(data);
                    return true;
                } catch(final NumberFormatException e) {
                    return false;
                }
            }
            case SHORT: {
                try {
                    Short.valueOf(data);
                    return true;
                } catch(final NumberFormatException e) {
                    return false;
                }
            }
            case INTEGER: {
                try {
                    Integer.valueOf(data);
                    return true;
                } catch(final NumberFormatException e) {
                    return false;
                }
            }
            case LONG: {
                try {
                    Long.valueOf(data);
                    return true;
                } catch(final NumberFormatException e) {
                    return false;
                }
            }
            case FLOAT: {
                try {
                    Float.valueOf(data);
                    return true;
                } catch(final NumberFormatException e) {
                    return false;
                }
            }
            case DOUBLE: {
                try {
                    Double.valueOf(data);
                    return true;
                } catch(final NumberFormatException e) {
                    return false;
                }
            }
            case BOOLEAN: {
                if (! data.equalsIgnoreCase("true") && ! data.equalsIgnoreCase("false")) {
                    return false;
                } else {
                    return true;
                }
            }
            default: {
                return true;
            }
        }
    }
}
