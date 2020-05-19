package ru.spliterash.pcmasterclient.view.controls.orders;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Value;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetRequest;
import ru.spliterash.pcmasterclient.api.methods.order.Order;
import ru.spliterash.pcmasterclient.api.methods.order.getMy.OrderGetMy;
import ru.spliterash.pcmasterclient.api.methods.order.getMy.OrderGetMyResponse;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.io.IOException;
import java.util.Map;

public class UserOrder extends HBox implements Page {
    @FXML
    private ListView<Order> view;

    @FXML
    private TableView<ComponentPrice> table;

    @FXML
    private TableColumn<ComponentPrice, String> typeColumn;

    @FXML
    private TableColumn<ComponentPrice, String> nameColumn;

    @FXML
    private TableColumn<ComponentPrice, Integer> priceColumn;

    /**
     * Если открывает юзер
     */
    @Override
    public void onOpen() {
        RequestManager.getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(true), this);
        RequestManager.getExecutor(OrderGetMy.class).updateCache(new Object(), this);

    }

    public UserOrder(boolean isUser) {
        Utils.loadFXML(this);
        Utils.setAnchorProperty(this);
        view.getSelectionModel().selectedItemProperty().addListener(this::onSelect);
        view.setCellFactory(param -> new ListCell<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    // other stuff to do...

                } else {

                    // set the width's
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());

                    // allow wrapping
                    setWrapText(true);

                    setText(item.toString());


                }
            }
        });
        nameColumn.setCellValueFactory(callback -> callback.getValue().getComponent().nameProperty());
        typeColumn.setCellValueFactory(callback -> callback.getValue().getComponent().getTypeFromCache().nameProperty());
        priceColumn.setCellValueFactory(callback -> new SimpleIntegerProperty(callback.getValue().getPrice()).asObject());
        if (isUser) {
            RequestManager.getExecutor(OrderGetMy.class).responseProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null)
                    setItems(newValue.getOrders());
            });
        }
    }

    public void setItems(ObservableList<Order> list) {
        if (list == null)
            list = FXCollections.observableArrayList();
        view.setItems(list);
        table.getItems().clear();
    }


    public void onSelect(ObservableValue<? extends Order> observable, Order oldValue, Order newValue) {
        ObservableList<ComponentPrice> items = table.getItems();
        items.clear();
        if (newValue != null) {
            for (Map.Entry<Component, Integer> entry : newValue.getFromCache().entrySet()) {
                ComponentPrice cp = new ComponentPrice(entry.getKey(), entry.getValue());
                items.add(cp);
            }
        }
    }

    @Value
    private static class ComponentPrice {
        Component component;
        int price;
    }
}
