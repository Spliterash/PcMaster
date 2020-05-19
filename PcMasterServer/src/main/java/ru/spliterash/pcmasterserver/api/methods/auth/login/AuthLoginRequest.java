package ru.spliterash.pcmasterserver.api.methods.auth.login;

import lombok.Value;

@Value
public class AuthLoginRequest {
    String login;
    String password;
}
