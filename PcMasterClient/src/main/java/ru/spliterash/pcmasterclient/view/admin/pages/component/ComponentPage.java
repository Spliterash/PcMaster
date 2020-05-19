package ru.spliterash.pcmasterclient.view.admin.pages.component;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetRequest;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.api.models.Supplier;
import ru.spliterash.pcmasterclient.interfaces.CRUDController;
import ru.spliterash.pcmasterclient.interfaces.ServerData;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.io.IOException;

public class ComponentPage extends HBox implements CRUDController<Component>, Page {
    private final ComponentModel model;
    @FXML
    private ListView<Component> view;
    @FXML
    private TextField search;
    @FXML
    private TextField componentPrice;
    @FXML
    private VBox itemBox;
    @FXML
    private TextField componentName;
    @FXML
    private TextField componentUrl;
    @FXML
    private Label componentId;
    @FXML
    private ComboBox<ComponentType> componentTypeBox;
    @FXML
    private ComboBox<Supplier> supplierBox;

    public ComponentPage() {
        Utils.loadFXML(this);
        Utils.setAnchorProperty(this);
        //Отрубаем ввод чеголибо кроме цифр
        componentPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                componentPrice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        view.setCellFactory(new Callback<ListView<Component>, ListCell<Component>>() {
            @Override
            public ListCell<Component> call(ListView<Component> param) {
                return new ListCell<Component>() {
                    @Override
                    protected void updateItem(Component item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        } else
                            setText(null);
                    }
                };
            }
        });
        model = new ComponentModel(this);
        itemBox
                .disableProperty()
                .bind(model.getSelectedNow().not());
        java.util.function.Supplier<ListCell<Supplier>> getSupplier = () -> new ListCell<Supplier>() {
            @Override
            protected void updateItem(Supplier item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else
                    setText(null);
            }
        };
        java.util.function.Supplier<ListCell<ComponentType>> getComponentType = () -> new ListCell<ComponentType>() {
            @Override
            protected void updateItem(ComponentType item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else
                    setText(null);
            }
        };
        supplierBox.setCellFactory(param -> getSupplier.get());
        componentTypeBox.setCellFactory(param -> getComponentType.get());
        supplierBox.setButtonCell(getSupplier.get());
        componentTypeBox.setButtonCell(getComponentType.get());
    }

    private <T extends ServerData> boolean compare(String typedText, T data) {
        return data.getName().toLowerCase().contains(typedText.toLowerCase());
    }

    @FXML
    void cancelAll(ActionEvent event) {
        model.restoreServer();
    }

    @FXML
    void createNewOne(ActionEvent event) {
        model.createNew(new Component(
                -1,
                "Новый товар",
                -1,
                -1,
                "https://static.onlinetrade.ru/img/items/m/1179332_1.jpg",
                -1
        ));
    }

    @FXML
    void deleteActive(ActionEvent event) {
        model.deleteSelected();
    }

    @FXML
    void saveAll(ActionEvent event) {
        model.save();
    }

    @Override
    public void setUserData(ObservableList<Component> data) {
        view.setItems(data);
    }

    @Override
    public SelectionModel<Component> getSelection() {
        return view.getSelectionModel();
    }

    @Override
    public void removeActive(Component value) {
        value.supplierProperty().unbind();
        value.componentTypeProperty().unbind();
        value.imageProperty().unbind();
        value.nameProperty().unbind();
        value.priceProperty().unbind();
    }

    @Override
    public void setActive(Component value) {
        if (value != null) {
            componentId.setText(value.getId() != -1 ? String.valueOf(value.getId()) : "Требуется сохранение");
            {
                StringProperty property = componentName.textProperty();
                property.set(value.getName());
                Platform.runLater(() -> value.nameProperty().bind(property));
            }
            {
                StringProperty property = componentUrl.textProperty();
                property.set(value.getImage());
                value.imageProperty().bind(property);
            }
            {
                StringProperty property = componentPrice.textProperty();
                property.set(String.valueOf(value.getPrice()));
                value.priceProperty().bind(Utils.createIntBind(property));
            }
            {
                SingleSelectionModel<ComponentType> selection = componentTypeBox.getSelectionModel();
                ComponentType type = value.getTypeFromCache();
                selection.select(type);
                value.componentTypeProperty().bind(Utils.createDataBinding(selection));
            }
            {
                SingleSelectionModel<Supplier> selection = supplierBox.getSelectionModel();
                Supplier type = value.getSupplierFromCache();
                selection.select(type);
                value.supplierProperty().bind(Utils.createDataBinding(selection));
            }
        } else {
            componentName.setText("");
            componentUrl.setText("");
            componentPrice.setText("");
            componentTypeBox.getSelectionModel().select(-1);
            supplierBox.getSelectionModel().select(-1);
        }
    }

    @Override
    public void setInQuery(boolean b) {
        disableProperty().set(b);
    }

    @Override
    public void onOpen() {
        {
            RequestManager.getInstance().getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(false));
        }
    }

    public void setType(ListProperty<ComponentType> componentTypes) {
        componentTypeBox.setItems(componentTypes);
    }

    public void setSupplier(ListProperty<Supplier> supplier) {
        supplierBox.setItems(supplier);
    }
}
