package ru.spliterash.pcmasterclient.interfaces;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;

public interface CRUDController<T extends ServerData> {
    void setUserData(ObservableList<T> data);

    SelectionModel<T> getSelection();

    void removeActive(T value);

    void setActive(T value);

    void setInQuery(boolean b);

    default StringProperty getSearch() {
        return null;
    }
}
