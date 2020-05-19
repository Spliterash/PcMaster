package ru.spliterash.pcmasterclient.view.admin.pages.componentType;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.interfaces.CRUDController;
import ru.spliterash.pcmasterclient.view.admin.CRUDModel;


public class ComponentTypeModel extends CRUDModel<ComponentType> {

    public ComponentTypeModel(CRUDController<ComponentType> controller) {
        super(controller);
        ReadOnlyObjectProperty<CatalogGetResponse> response = RequestManager.getInstance().getExecutor(CatalogGet.class).responseProperty();
        response.addListener(this::update);
    }

    private void update(ObservableValue<? extends CatalogGetResponse> observable, CatalogGetResponse oldValue, CatalogGetResponse newValue) {
        ListProperty<ComponentType> serverData = getServerData();
        //Удаляем старый бинд
        serverData.unbind();
        serverData.clear();
        //Биндим на новый ответ
        if (newValue != null) {
            serverData.bind(newValue.getComponentTypes());
        }
        restoreServer();
    }

    @Override
    public Callback<ComponentType, Observable[]> getListBindings() {
        return param -> new Observable[]{param.nameProperty()};
    }
}
