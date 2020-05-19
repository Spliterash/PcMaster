package ru.spliterash.pcmasterclient.view.admin.pages.supplier;

import javafx.application.Platform;
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
import ru.spliterash.pcmasterclient.api.models.Supplier;
import ru.spliterash.pcmasterclient.interfaces.CRUDController;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.io.IOException;

public class SupplierPage extends HBox implements CRUDController<Supplier>, Page {
    private SupplierModel model;

    public SupplierPage() {
     Utils.loadFXML(this);
        //Растягиваем элемент на всю длину
        Utils.setAnchorProperty(this);
        view.setCellFactory(new Callback<ListView<Supplier>, ListCell<Supplier>>() {
            @Override
            public ListCell<Supplier> call(ListView<Supplier> param) {
                return new ListCell<Supplier>() {
                    @Override
                    protected void updateItem(Supplier item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        } else
                            setText(null);
                    }
                };
            }
        });
        model = new SupplierModel(this);
        itemBox
                .disableProperty()
                .bind(model.getSelectedNow().not());
    }

    @Override
    public StringProperty getSearch() {
        return search.textProperty();
    }

    @FXML
    private ListView<Supplier> view;

    @FXML
    private TextField search;

    @FXML
    private VBox itemBox;

    @FXML
    private TextField supplierName;

    @FXML
    private TextField supplierImageUrl;

    @FXML
    private Label supplierId;

    @FXML
    void cancelAll(ActionEvent event) {
        model.restoreServer();
    }

    @FXML
    void createNewOne(ActionEvent event) {
        model.createNew(new Supplier(
                -1,
                "Поставщик",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/ASUS_Logo.svg/1280px-ASUS_Logo.svg.png"
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
    public void setUserData(ObservableList<Supplier> data) {
        view.setItems(data);
    }

    @Override
    public SelectionModel<Supplier> getSelection() {
        return view.getSelectionModel();
    }

    @Override
    public void removeActive(Supplier value) {
        value.imageUrlProperty().unbind();
        value.nameProperty().unbind();
    }

    @Override
    public void setActive(Supplier value) {
        if (value != null) {
            {
                StringProperty property = supplierName.textProperty();
                property.set(value.getName());
                Platform.runLater(() -> value.nameProperty().bind(property));
            }
            {
                StringProperty property = supplierImageUrl.textProperty();
                property.set(value.getImageUrl());
                value.imageUrlProperty().bind(property);
            }
            {
                supplierId.setText(value.getId() == -1 ? "Требуется сохранение" : String.valueOf(value.getId()));
            }
        } else {
            supplierId.setText("");
            supplierName.setText("");
            supplierImageUrl.setText("");
        }
    }

    @Override
    public void setInQuery(boolean b) {
        this.setDisable(b);
    }

    @Override
    public void onOpen() {
        RequestManager.getInstance().getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(false));
    }
}
