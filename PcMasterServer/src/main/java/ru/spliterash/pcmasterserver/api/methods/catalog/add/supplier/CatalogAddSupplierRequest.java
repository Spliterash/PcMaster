package ru.spliterash.pcmasterserver.api.methods.catalog.add.supplier;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CatalogAddSupplierRequest {
    private String supplier;
    @SerializedName("image_url")
    private String imageUrl;
}
