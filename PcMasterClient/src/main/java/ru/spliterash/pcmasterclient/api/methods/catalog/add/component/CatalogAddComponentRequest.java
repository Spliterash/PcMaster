package ru.spliterash.pcmasterclient.api.methods.catalog.add.component;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Value;

@Value
public class CatalogAddComponentRequest {

    String name;
    int price;
    @SerializedName("supplier_id")
    int supplierId;
    @SerializedName("type_id")
    int typeId;
    @SerializedName("image_url")
    String imageUrl;

}
