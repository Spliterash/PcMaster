package ru.spliterash.pcmasterclient.api.methods.catalog.get;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import lombok.*;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.api.models.Supplier;

import java.util.ArrayList;
import java.util.List;
@Getter
public class CatalogGetResponse {
    @SerializedName("component_type_list")
    private ListProperty<ComponentType> componentTypes;
    @SerializedName("supplier_list")
    private ListProperty<Supplier> suppliers;

    private ListProperty<Component> components;
    private ListProperty<CatalogCharacteristic> characteristics;

    @Data
    @RequiredArgsConstructor
    public static class CatalogCharacteristic {
        private final int id;
        private final String name;
        private ObservableList<String> values;
    }
}
