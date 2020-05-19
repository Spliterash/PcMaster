package ru.spliterash.pcmasterserver.api.methods.catalog.add;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataAddResponse {
    private DataAddResponse.Response response;
    private Integer id;
    public enum  Response {
        OK,EXISTS
    }
}
