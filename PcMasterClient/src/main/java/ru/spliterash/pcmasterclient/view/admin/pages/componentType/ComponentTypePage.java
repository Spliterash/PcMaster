package ru.spliterash.pcmasterclient.view.admin.pages.componentType;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
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
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.interfaces.CRUDController;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.io.IOException;

public class ComponentTypePage extends HBox implements CRUDController<ComponentType>, Page {
    @FXML
    private ListView<ComponentType> view;
    @FXML
    private TextField componentTypeName;
    @FXML
    private TextField componentTypeUrl;
    @FXML
    private Label componentTypeId;
    @FXML
    private TextArea componentTypeDescription;
    @FXML
    private VBox itemBox;
    @FXML
    private TextField search;
    @FXML
    private CheckBox componentTypeRequired;
    private ComponentTypeModel model;

    public ComponentTypePage() {
        Utils.loadFXML(this);
        //Растягиваем элемент на всю длину
        Utils.setAnchorProperty(this);
        //Преобразование cell в строку
        view.setCellFactory(new Callback<ListView<ComponentType>, ListCell<ComponentType>>() {
            @Override
            public ListCell<ComponentType> call(ListView<ComponentType> param) {
                return new ListCell<ComponentType>() {
                    @Override
                    protected void updateItem(ComponentType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                            String text =
                                    "ID: " + item.getId() + "\n" +
                                            "Имя: " + item.getName() + "\n" +
                                            "Описание: " + item.getDescription();
                            setTooltip(new Tooltip(text));
                        } else
                            setText(null);
                    }
                };
            }
        });
        model = new ComponentTypeModel(this);
        //Если пользователь захочет обновить, то слушаем
        itemBox
                .disableProperty()
                .bind(model.getSelectedNow().not());
    }

    @Override
    public StringProperty getSearch() {
        return search.textProperty();
    }

    @FXML
    public void cancelAll(ActionEvent event) {
        model.restoreServer();
    }

    @FXML
    void createNewOne(ActionEvent event) {
        model.createNew(new ComponentType(
                -1,
                "Название категории",
                "https://via.placeholder.com/350x350",
                "Описание новой категории",
                false
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
    public void setUserData(ObservableList<ComponentType> data) {
        view.setItems(data);
    }

    @Override
    public SelectionModel<ComponentType> getSelection() {
        return view.getSelectionModel();
    }

    @Override
    public void removeActive(ComponentType value) {
        if (value != null) {
            value.nameProperty().unbind();
            value.descriptionProperty().unbind();
            value.imageUrlProperty().unbind();
            value.requiredProperty().unbind();
        }
    }

    @Override
    public void setActive(ComponentType type) {
        if (type != null) {
            componentTypeId.setText(type.getId() == -1 ? "Требуется сохранение" : String.valueOf(type.getId()));
            {
                StringProperty property = componentTypeName.textProperty();
                property.set(type.getName());
                //Если не использовать лунЛатер то при удалении тут жопа, поэтому лучше использовать
                //Как я понял это происходит из за того, что в листе мы указали этот Property как observable
                //И ListView следит за ним, при удалении он пытается к нему обратиться, но он биндится на это
                //Крч я сам не особо понимаю, но если использовать это то всё норм
                Platform.runLater(() -> type.nameProperty().bind(property));
            }
            {
                StringProperty property = componentTypeUrl.textProperty();
                property.set(type.getImageUrl());
                type.imageUrlProperty().bind(property);
            }
            {
                StringProperty property = componentTypeDescription.textProperty();
                property.set(type.getDescription());
                type.descriptionProperty().bind(property);
            }
            {
                BooleanProperty checked = componentTypeRequired.selectedProperty();
                checked.set(type.isRequired());
                type.requiredProperty().bind(checked);
            }
        } else {
            componentTypeName.setText("");
            componentTypeId.setText("");
            componentTypeUrl.setText("");
            componentTypeDescription.setText("");
        }
    }

    @Override
    public void setInQuery(boolean b) {
        setDisable(b);
    }

    @Override
    public void onOpen() {
        RequestManager.getInstance().getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(false));
    }
}
