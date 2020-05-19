package ru.spliterash.pcmasterclient.api.methods.auth.login;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
public class AuthLoginRequest {
    String login;
    String password;
}
