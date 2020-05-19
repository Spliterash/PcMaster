package ru.spliterash.pcmasterclient.view.admin.pages.supplier;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import org.jetbrains.annotations.Nullable;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.models.Supplier;
import ru.spliterash.pcmasterclient.interfaces.CRUDController;
import ru.spliterash.pcmasterclient.view.admin.CRUDModel;

public class SupplierModel extends CRUDModel<Supplier> {
    public SupplierModel(CRUDController<Supplier> controller) {
        super(controller);
        ReadOnlyObjectProperty<CatalogGetResponse> response = RequestManager.getInstance().getExecutor(CatalogGet.class).responseProperty();
        response.addListener(this::update);
    }

    private void update(ObservableValue<? extends CatalogGetResponse> observable, CatalogGetResponse oldValue, CatalogGetResponse newValue) {
        ListProperty<Supplier> serverData = getServerData();
        //Удаляем старый бинд
        serverData.unbind();
        serverData.clear();
        //Биндим на новый ответ
        if (newValue != null)
            serverData.bind(newValue.getSuppliers());
        restoreServer();
    }

    @Override
    public Callback<Supplier, Observable[]> getListBindings() {
        return param -> new Observable[]{param.nameProperty()};
    }
}
