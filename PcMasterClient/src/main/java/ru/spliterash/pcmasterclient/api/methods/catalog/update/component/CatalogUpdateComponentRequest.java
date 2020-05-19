package ru.spliterash.pcmasterclient.api.methods.catalog.update.component;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Value;

@Value
public class CatalogUpdateComponentRequest {
    int id;
    String name;
    int price;
    @SerializedName("supplier_id")
    int supplierId;
    @SerializedName("type_id")
    int typeId;
    @SerializedName("image_url")
    String imageUrl;
}
