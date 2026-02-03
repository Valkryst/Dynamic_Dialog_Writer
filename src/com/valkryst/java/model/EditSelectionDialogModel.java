package model;

import com.valkryst.dds.manager.DDSManager;
import com.valkryst.dds.object.Rule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

public class EditSelectionDialogModel {
    @Getter private final ObservableList<Rule> rules = FXCollections.observableArrayList();

    @Getter private final ObservableList<Rule> rulesSelectedOnDialogClose = FXCollections.observableArrayList();

    public EditSelectionDialogModel(final DDSManager ddsManager) {
        loadRuleModels(ddsManager);
    }

    public void loadRuleModels(final DDSManager ddsManager) {
        ddsManager.getArrayList_rules().parallelStream()
                                       .forEach(rules::add);
    }
}
