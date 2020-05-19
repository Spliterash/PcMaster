package ru.spliterash.pcmasterclient.view.admin.pages.component;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.util.Callback;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.view.admin.CRUDModel;

public class ComponentModel extends CRUDModel<Component> {
    public ComponentModel(ComponentPage controller) {
        super(controller);
        ReadOnlyObjectProperty<CatalogGetResponse> response = RequestManager.getInstance().getExecutor(CatalogGet.class).responseProperty();
        response.addListener(this::update);
    }

    private void update(ObservableValue<? extends CatalogGetResponse> observable, CatalogGetResponse oldValue, CatalogGetResponse newValue) {
        ListProperty<Component> serverData = getServerData();
        //Удаляем старый бинд
        serverData.unbind();
        serverData.clear();
        //Биндим на новый ответ
        if (newValue != null) {
            serverData.bind(newValue.getComponents());
            //Согласен говнокод
            ComponentPage page = (ComponentPage) getController();
            page.setType(newValue.getComponentTypes());
            page.setSupplier(newValue.getSuppliers());
        }

        restoreServer();

    }

    @Override
    public void save() {
        StringBuilder error = new StringBuilder();

        for (Component data : getUserData()) {
            if (data.getSupplier() == -1) {
                error.append("Вы не заполнили поле поставщика у ").append(data.getName()).append("\n");
            }
            if (data.getComponentType() == -1) {
                error.append("Вы не заполнили тип компонента у ").append(data.getName()).append("\n");
            }
        }
        if (error.length() > 0) {
            String title = "Ошибка сохранения";
            String head = "Незаполненые поля";
            Utils.showWaitAlert(Alert.AlertType.WARNING, title, head, error.toString());
        } else
            super.save();
    }

    @Override
    public Callback<Component, Observable[]> getListBindings() {
        return param -> new Observable[]{param.nameProperty()};
    }
}
