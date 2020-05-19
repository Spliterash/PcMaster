package ru.spliterash.pcmasterclient.view.user.pages.build;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import ru.spliterash.pcmasterclient.ImageManager;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.view.controls.animation.HoverAnimationPane;
import ru.spliterash.pcmasterclient.view.user.pages.build.controls.hover.Hover;
import ru.spliterash.pcmasterclient.view.user.pages.build.controls.selected.Selected;
import ru.spliterash.pcmasterclient.view.user.pages.build.controls.select.window.SelectWindow;

public class BuildComponent extends HoverAnimationPane {
    @Getter
    private final ComponentType type;
    private final BuildPage page;
    private final ImageView defaultView;
    @Getter
    private Component selected = null;
    @Getter
    private Image image;

    public BuildComponent(ComponentType type, BuildPage page) {
        super();
        this.type = type;
        this.page = page;
        this.defaultView = getDefaultView();
        update();
    }

    private ImageView getDefaultView() {
        ImageView view = new ImageView();
        view.setFitWidth(getPrefWidth());
        view.setFitHeight(getPrefHeight());
        view.preserveRatioProperty().set(true);
        view.smoothProperty().set(true);
        Utils.setImage(type.getImageUrl(), view, (image) -> this.image = image);
        return view;
    }

    public void setSelected(Component selected) {
        this.selected = selected;
        update();
    }

    @Override
    protected void update() {
        super.update();
        page.refreshPrice();
    }

    @Override
    protected void onClick(MouseEvent mouseEvent) {
        if (selected != null) {
            selected = null;
            update();
        } else {
            openSelectWindow();
        }
    }

    private void openSelectWindow() {
        Stage stage = new Stage();
        stage.setScene(new Scene(new SelectWindow(this)));
        stage.setTitle("Выберите компонент " + type.getName());
        stage.initOwner(getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @Override
    protected Node getDefault() {
        if (selected == null) {
            return defaultView;
        } else {
            return new Selected(selected);
        }
    }

    @Override
    protected Node getHover() {
        if (selected == null) {
            return new Hover(ImageManager.PLUS.getResizedFx(135, 135), type.getName());
        } else {
            return new Hover(ImageManager.CLOSE.getResizedFx(135, 135), "Убрать");
        }
    }

    //Запускает анимацию тряски
    public void shake() {
        AnchorPane root = getRoot();
        Timeline line = new Timeline(
                new KeyFrame(Duration.millis(150),
                        new KeyValue(root.scaleXProperty(), 0.7),
                        new KeyValue(root.scaleYProperty(), 0.7)
                ),
                new KeyFrame(Duration.millis(350),
                        new KeyValue(root.rotateProperty(), 35)
                ),
                new KeyFrame(Duration.millis(550),
                        new KeyValue(root.rotateProperty(), -35)
                ),
                new KeyFrame(Duration.millis(750),
                        new KeyValue(root.rotateProperty(), 35)
                ),
                new KeyFrame(Duration.millis(950),
                        new KeyValue(root.scaleXProperty(), 0.7),
                        new KeyValue(root.scaleYProperty(), 0.7),
                        new KeyValue(root.rotateProperty(), -35)
                ),
                new KeyFrame(Duration.millis(1050),
                        new KeyValue(root.scaleXProperty(), 1),
                        new KeyValue(root.scaleYProperty(), 1),
                        new KeyValue(root.rotateProperty(), 0))
        );
        line.play();
    }
}
