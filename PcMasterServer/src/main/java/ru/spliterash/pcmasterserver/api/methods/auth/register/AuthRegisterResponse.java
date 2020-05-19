package ru.spliterash.pcmasterserver.api.methods.auth.register;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthRegisterResponse {
    public Response response;

    public enum Response {
        OK, USERNAME_TAKEN
    }
}

