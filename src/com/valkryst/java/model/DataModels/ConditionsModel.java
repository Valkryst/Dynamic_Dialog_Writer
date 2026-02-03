package model.DataModels;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

public class ConditionsModel {
    @Getter private final SimpleStringProperty context;
    @Getter private final SimpleStringProperty comparisonType;
    private final SimpleStringProperty valueToCompareTo;

    public ConditionsModel(final String context, final String comparisonType, final String valueToCompareTo) {
        this.context = new SimpleStringProperty(context);
        this.comparisonType = new SimpleStringProperty(comparisonType);
        this.valueToCompareTo = new SimpleStringProperty(valueToCompareTo);
    }

    public boolean equals(final ConditionsModel otherModel) {
        boolean isEqual;
        isEqual = context.get().equals(otherModel.getContext());
        isEqual &= comparisonType.get().equals(otherModel.getComparisonType());
        isEqual &= valueToCompareTo.get().equals(otherModel.getValueToCompareTo());
        return isEqual;
    }

    public String getValueToCompareTo() {
        return valueToCompareTo.get();
    }
}
