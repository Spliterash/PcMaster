package ru.spliterash.pcmasterserver.api.methods.catalog.add.component;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CatalogAddComponentRequest {

    private String name;
    private int price;
    @SerializedName("supplier_id")
    private int supplierId;
    @SerializedName("type_id")
    private int typeId;
    @SerializedName("image_url")
    String imageUrl;
}
