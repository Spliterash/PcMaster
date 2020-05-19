package ru.spliterash.pcmasterclient.view.admin.pages.orders;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetRequest;
import ru.spliterash.pcmasterclient.api.methods.order.getAll.OrderGetAll;
import ru.spliterash.pcmasterclient.api.methods.order.getAll.OrderGetAllResponse;
import ru.spliterash.pcmasterclient.api.methods.order.getAll.OrderGetAllResponse.OrderGetRow;
import ru.spliterash.pcmasterclient.view.admin.Page;
import ru.spliterash.pcmasterclient.view.controls.orders.UserOrder;

public class OrderPage extends ListView<OrderGetRow> implements Page {
    @Override
    public void onOpen() {
        RequestManager.getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(false), this);
        RequestManager.getExecutor(OrderGetAll.class).updateCache(new Object(), this);
    }

    public OrderPage() {
        Utils.setAnchorProperty(this);
        RequestManager.getExecutor(OrderGetAll.class).responseProperty().addListener(this::onUpdate);
        setCellFactory(new Callback<ListView<OrderGetRow>, ListCell<OrderGetRow>>() {
            @Override
            public ListCell<OrderGetRow> call(ListView<OrderGetRow> param) {
                return new ListCell<OrderGetRow>() {
                    @Override
                    protected void updateItem(OrderGetRow item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getUser().toString());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        setOnMouseClicked(this::onClick);
    }

    private void onClick(MouseEvent click) {
        if (click.getClickCount() == 2) {
            OrderGetRow selected = getSelectionModel().getSelectedItem();
            if (selected != null) {
                Stage stage = new Stage();
                UserOrder order = new UserOrder(false);
                order.setItems(FXCollections.observableList(selected.getOrders()));
                Scene scene = new Scene(order);
                stage.setScene(scene);
                stage.setTitle("Заказы " + selected.getUser().toString());
                stage.initOwner(getScene().getWindow());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
        }
    }

    public void onUpdate(ObservableValue<?> observable, OrderGetAllResponse oldValue, OrderGetAllResponse newValue) {
        getItems().clear();
        if (newValue != null) {
            getItems().addAll(newValue.getOrders());
        }
    }
}
