package ru.spliterash.pcmasterclient.api.methods.catalog.update.componentType;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Value;

@Value
public class CatalogUpdateComponentTypeRequest {
    Integer id;
    String type;
    String description;
    @SerializedName("image_url")
    String imageUrl;
    boolean required;
}
