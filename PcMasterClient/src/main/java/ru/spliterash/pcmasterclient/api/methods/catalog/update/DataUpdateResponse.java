package ru.spliterash.pcmasterclient.api.methods.catalog.update;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataUpdateResponse {
    private Response response;

    public enum Response {
        OK,NOT_FOUND,ERROR
    }
}
