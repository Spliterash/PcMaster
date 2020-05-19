package ru.spliterash.pcmasterclient.api.methods.catalog.add;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataAddResponse {
    private Response response;
    private Integer id;
    public enum  Response {
        OK,EXISTS
    }
}
