package ru.spliterash.pcmasterclient.api.methods.catalog.add.supplier;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class CatalogAddSupplierRequest {
    String supplier;
    @SerializedName("image_url")
    String imageUrl;
}
