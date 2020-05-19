package ru.spliterash.pcmasterserver.api.methods.catalog.update.supplier;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CatalogUpdateSupplierRequest {
    private int id;
    private String supplier;
    @SerializedName("image_url")
    private String imageUrl;
}
