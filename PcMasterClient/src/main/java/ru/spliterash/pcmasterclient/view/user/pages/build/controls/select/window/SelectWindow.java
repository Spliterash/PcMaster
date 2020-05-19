package ru.spliterash.pcmasterclient.view.user.pages.build.controls.select.window;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.spliterash.pcmasterclient.ImageManager;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.view.user.pages.build.BuildComponent;
import ru.spliterash.pcmasterclient.view.user.pages.build.controls.select.element.SelectElement;

public class SelectWindow extends VBox {
    private final BuildComponent buildComponent;
    @FXML
    private ImageView view;

    @FXML
    private Label name;

    @FXML
    private Label description;

    @FXML
    private FlowPane pane;

    public SelectWindow(BuildComponent buildComponent) {
        Utils.loadFXML(this);
        this.buildComponent = buildComponent;
        //Ставим картинку
        {
            Image image = buildComponent.getImage();
            if (image != null) {
                view.setImage(image);
            } else {
                view.setImage(ImageManager.BROKEN.getResizedFx((int) view.getFitWidth(), -1));
            }
        }
        ComponentType type = buildComponent.getType();
        //Ставим имя
        name.setText(type.getName());
        //Описание
        description.setText(type.getDescription());
        //А терь самое сложное, заполняем
        ObservableList<Node> elements = pane.getChildren();
        type
                .getComponentsFromCache()
                .stream()
                .map(c -> new SelectElement(c, SelectWindow.this))
                .forEach(elements::add);
    }

    public void select(SelectElement selected) {
        buildComponent.setSelected(selected.getComponent());
        Stage s = (Stage) getScene().getWindow();
        s.close();
    }
}
