package ru.spliterash.pcmasterclient.api.methods.auth.register;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AuthRegisterResponse {
    @Getter
    private Response response;

    public enum Response {
        OK, USERNAME_TAKEN
    }
}

