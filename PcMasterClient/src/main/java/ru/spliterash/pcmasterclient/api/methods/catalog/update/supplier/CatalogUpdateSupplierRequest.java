package ru.spliterash.pcmasterclient.api.methods.catalog.update.supplier;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Value;

@Value
public class CatalogUpdateSupplierRequest {
    int id;
    String supplier;
    @SerializedName("image_url")
    String imageUrl;
}
