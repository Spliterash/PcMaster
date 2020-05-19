package ru.spliterash.pcmasterserver.api.methods.catalog.get;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import ru.spliterash.pcmasterserver.api.models.*;

import java.util.List;

@Value
@Builder
public class CatalogGetResponse {
    @SerializedName("component_type_list")
    @Singular
    List<ComponentType> componentTypes;
    @SerializedName("supplier_list")
    @Singular
    List<Supplier> suppliers;

    List<Component> components;

}
