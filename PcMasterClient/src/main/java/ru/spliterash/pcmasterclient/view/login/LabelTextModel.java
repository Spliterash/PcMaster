package ru.spliterash.pcmasterclient.view.login;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;

@Getter
public abstract class LabelTextModel {
    protected BooleanProperty isValidProperty = new SimpleBooleanProperty(false);
    protected ObservableList<Node> list = new SimpleListProperty<>();


    public void autoBind(Pane pane, TextInputControl field) {
        list = pane.getChildren();
        field.textProperty().addListener(this::onChanged);
    }

    public abstract void onChanged(ObservableValue<? extends String> observable, String oldValue, String newValue);

    protected void clearList() {
        list.clear();
    }


    protected void addLine(String string) {
        addLine(string, Color.ORANGE);
    }

    protected void ok() {
        addLine("Всё верно", Color.valueOf("#7bff15"));
        isValidProperty.set(true);
    }

    protected void addLine(String string, Color color) {
        if (!list.isEmpty())
            list.add(new Text(System.lineSeparator()));
        Text text = new Text(string);
        text.setFill(color);
        list.add(text);

    }
}
