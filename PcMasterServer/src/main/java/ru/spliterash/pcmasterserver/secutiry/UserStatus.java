package ru.spliterash.pcmasterserver.secutiry;

import org.springframework.security.core.GrantedAuthority;

public enum UserStatus implements GrantedAuthority {
    ADMIN, USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
