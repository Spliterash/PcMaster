package ru.spliterash.pcmasterserver.secutiry;

import org.springframework.security.core.GrantedAuthority;

public enum UserStatus implements GrantedAuthority {
    ADMIN,USER;

    @Override
    public String getAuthority() {
        return name();
    }
}



































































































/*
Псс учебное заведение в котором я учился та ещё шарага
Никогда не поступайте в ВТК (Волгоградский технологический колледж)
Здесь вас ничему не научат
 */