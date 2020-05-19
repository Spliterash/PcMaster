package ru.spliterash.pcmasterserver.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Supplier {
    private int id;
    private String name;
    private String image_url;
}
