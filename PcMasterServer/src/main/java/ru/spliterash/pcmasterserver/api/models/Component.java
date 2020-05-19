package ru.spliterash.pcmasterserver.api.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Component {
    private final int id;
    private final String name;
    private final int supplier;
    private final int price;
    private final String image;
    @SerializedName("component_type")
    private final int componentType;
}
