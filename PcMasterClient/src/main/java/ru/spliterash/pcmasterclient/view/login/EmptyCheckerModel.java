package ru.spliterash.pcmasterclient.view.login;

import javafx.beans.value.ObservableValue;

public class EmptyCheckerModel extends LabelTextModel {
    @Override
    public void onChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        clearList();
        if (newValue.isEmpty()) {
            addLine("Введите данные");
            isValidProperty.set(false);
        } else {
            ok();
        }
    }
}
