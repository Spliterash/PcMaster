package ru.spliterash.pcmasterclient.api.methods.catalog.delete;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataDeleteResponse {
    private Response response;

    public enum Response {
        OK, NO_EXISTS, ERROR
    }
}
