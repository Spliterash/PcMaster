package ru.spliterash.pcmasterclient.view.user.pages.build;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetRequest;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BuildPage extends VBox implements Page {
    @FXML
    private FlowPane flow;
    @FXML
    private Label priceLabel;

    private BuildPageModel model;

    public BuildPage() {
        Utils.loadFXML(this);
        Utils.setAnchorProperty(this);
        model = new BuildPageModel(this);
    }

    @Override
    public void onOpen() {
        RequestManager.getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(true), this);
    }


    public void updateTypes(ListProperty<ComponentType> types) {
        ObservableList<Node> c = flow.getChildren();
        c.clear();
        if (types != null)
            for (ComponentType type : types) {
                c.add(new BuildComponent(type, this));
            }
    }

    /**
     * Вызывается при нажатии кнопки сбросить заказ
     */
    public void resetBuild(ActionEvent event) {
        resetBuild();
    }

    private void resetBuild() {
        for (Node child : flow.getChildren()) {
            if (child instanceof BuildComponent) {
                BuildComponent component = (BuildComponent) child;
                component.setSelected(null);
            }
        }
    }

    /**
     * Вызывается при нажатии кнопки оформить заказ
     * Проверяет необходимые компоненты
     * Если всё ок оформляет
     */
    public void buyBuild(ActionEvent event) {
        Button button = (Button) event.getSource();
        Set<Component> buildSet = new HashSet<>();
        boolean missRequired = false;
        for (Node child : flow.getChildren()) {
            if (child instanceof BuildComponent) {
                BuildComponent component = (BuildComponent) child;
                Component selected = component.getSelected();
                if (selected != null) {
                    buildSet.add(selected);
                } else if (component.getType().isRequired()) {
                    //Меняем надпись на отсутсвуют необходимые компоненты
                    if (!missRequired) {
                        button.setDisable(true);
                        priceLabel.setTextFill(Color.RED);
                        priceLabel.setText("Отсутсвуют компоненты");
                    }
                    missRequired = true;
                    component.shake();
                }
            }
        }
        if (missRequired) {
            new Timeline(
                    new KeyFrame(Duration.seconds(1.5), e -> {
                        priceLabel.setTextFill(new Color(0, 0, 0, 1));
                        refreshPrice();
                        button.setDisable(false);
                    })
            ).play();
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
            alert.setTitle("Подверждение покупки");
            alert.setHeaderText("Подвердите покупку на сумму " + getPrice() + " р.");
            alert.initOwner(getScene().getWindow());
            StringBuilder builder = new StringBuilder("Ваш заказ состоит из:");
            for (Component component : buildSet) {
                builder
                        .append(component.getTypeFromCache().getName())
                        .append(": ")
                        .append(component.getName())
                        .append(" за ")
                        .append(component.getPrice())
                        .append(" р.")
                        .append("\n");
            }
            alert.setContentText(builder.toString());
            alert
                    .showAndWait()
                    .ifPresent(b -> {
                        if (b.equals(ButtonType.OK)) {
                            model.makePurchase(buildSet);
                            resetBuild();
                        }
                    });

        }

    }

    public int getPrice() {
        int price = 0;
        for (Node child : flow.getChildren()) {
            if (child instanceof BuildComponent) {
                BuildComponent component = (BuildComponent) child;
                Component selected = component.getSelected();
                if (selected != null) {
                    price += selected.getPrice();
                }
            }
        }
        return price;
    }

    public void refreshPrice() {
        int price = getPrice();
        priceLabel.setText("Цена сборки: " + price);
    }
}
