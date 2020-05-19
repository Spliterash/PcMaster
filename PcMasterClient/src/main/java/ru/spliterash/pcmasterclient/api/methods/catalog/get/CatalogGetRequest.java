package ru.spliterash.pcmasterclient.api.methods.catalog.get;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class CatalogGetRequest {
    @SerializedName("replace_placeholders")
    boolean replacePlaceholders;
}
