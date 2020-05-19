package ru.spliterash.pcmasterclient.view.user.pages.build.controls.select.element;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.view.controls.animation.AnimationPane;
import ru.spliterash.pcmasterclient.view.user.pages.build.controls.select.window.SelectWindow;
import ru.spliterash.pcmasterclient.view.user.pages.build.controls.selected.Selected;

public class SelectElement extends AnimationPane {
    @Getter
    private final Component component;
    private final SelectWindow windows;

    public SelectElement(Component component, SelectWindow window) {
        this.component = component;
        this.windows = window;
        update();
    }

    @Override
    protected void onClick(MouseEvent mouseEvent) { ;
        windows.select(this);
    }

    @Override
    protected Node getDefault() {
        return new Selected(component);
    }
}
