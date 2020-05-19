package ru.spliterash.pcmasterclient.api.models;

import lombok.Value;

import java.util.Set;

@Value
public class PcMasterUser {
    int id;
    String firstName;
    String lastName;
    String middleName;
    String username;
    UserStatus status;


    public boolean hasRole(UserStatus requiredRole) {
        if (status == UserStatus.ADMIN) {
            return true;
        } else
            return requiredRole.equals(status);
    }

    public enum UserStatus {
        ADMIN, USER;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName + " " + middleName;
    }
}
