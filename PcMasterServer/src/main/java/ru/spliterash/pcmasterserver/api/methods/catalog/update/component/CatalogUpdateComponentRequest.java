package ru.spliterash.pcmasterserver.api.methods.catalog.update.component;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CatalogUpdateComponentRequest {
    private int id;
    private String name;
    private int price;
    @SerializedName("supplier_id")
    private int supplierId;
    @SerializedName("type_id")
    private int typeId;
    @SerializedName("image_url")
    String imageUrl;
}
