package ru.spliterash.pcmasterserver.api.methods.catalog.add.componentType;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CatalogAddComponentTypeRequest {
    private String type;
    private String description;
    @SerializedName("image_url")
    private String imageUrl;
    private boolean required;
}
