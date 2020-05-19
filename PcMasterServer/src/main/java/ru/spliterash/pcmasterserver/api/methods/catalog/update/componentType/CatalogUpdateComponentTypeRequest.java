package ru.spliterash.pcmasterserver.api.methods.catalog.update.componentType;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CatalogUpdateComponentTypeRequest {
    private Integer id;
    private String type;
    private String description;
    @SerializedName("image_url")
    private String imageUrl;
    private boolean required;
}
