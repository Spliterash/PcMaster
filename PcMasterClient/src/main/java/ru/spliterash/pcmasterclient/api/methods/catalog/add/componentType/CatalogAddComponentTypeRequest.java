package ru.spliterash.pcmasterclient.api.methods.catalog.add.componentType;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class CatalogAddComponentTypeRequest {
    String type;
    String description;
    @SerializedName("image_url")
    String imageUrl;
    boolean required;

}
