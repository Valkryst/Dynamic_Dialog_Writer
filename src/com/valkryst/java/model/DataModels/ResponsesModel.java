package model.DataModels;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

public class ResponsesModel {
    @Getter private final SimpleStringProperty responseType;
    @Getter private final SimpleStringProperty responseValue;

    public ResponsesModel(final String responseType, final String responseValue) {
        this.responseType = new SimpleStringProperty(responseType);
        this.responseValue = new SimpleStringProperty(responseValue);
    }

    public boolean equals(final ResponsesModel otherModel) {
        boolean isEqual;
        isEqual = responseType.get().equals(otherModel.getResponseType());
        isEqual &= responseValue.get().equals(otherModel.getResponseValue());
        return isEqual;
    }
}
