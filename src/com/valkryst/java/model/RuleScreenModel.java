package model;

import com.valkryst.dds.manager.DDSManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import model.DataModels.ConditionsModel;
import model.DataModels.ResponsesModel;

public class RuleScreenModel {
    @Getter private final ObservableList<String> comparisonTypes = FXCollections.observableArrayList();

    @Getter private final ObservableList<String> contexts = FXCollections.observableArrayList();

    @Getter private final ObservableList<String> events = FXCollections.observableArrayList();

    @Getter private final ObservableList<String> responseTypes = FXCollections.observableArrayList();


    @Getter private final ObservableList<ConditionsModel> conditions = FXCollections.observableArrayList();

    @Getter private final ObservableList<ResponsesModel> responses = FXCollections.observableArrayList();


    public RuleScreenModel(final DDSManager ddsManager) {
        if(comparisonTypes.size() == 0) {
            comparisonTypes.add("=");
            comparisonTypes.add("<");
            comparisonTypes.add(">");
            comparisonTypes.add("<=");
            comparisonTypes.add(">=");
            comparisonTypes.add("!=");
        }

        if(contexts.size() == 0) {
            ddsManager.getHashMap_contexts()
                      .entrySet()
                      .stream()
                      .forEachOrdered(entry -> {
                          contexts.add(entry.getKey() + " - (" + entry.getValue().getValueType() + ")");
                      });
        }

        if(events.size() == 0) {
            events.addAll(ddsManager.getArrayList_events());
        }

        if(responseTypes.size() == 0) {
            responseTypes.addAll(ddsManager.getArrayList_responseTypes());
        }
    }
}
