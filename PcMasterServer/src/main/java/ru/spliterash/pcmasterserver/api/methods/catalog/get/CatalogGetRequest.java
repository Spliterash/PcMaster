package ru.spliterash.pcmasterserver.api.methods.catalog.get;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class CatalogGetRequest {
    @SerializedName("replace_placeholders")
   private boolean replacePlaceholders;
}
