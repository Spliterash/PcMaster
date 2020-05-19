package ru.spliterash.pcmasterserver.secutiry;

import lombok.ToString;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Value
public class PcMasterUser implements UserDetails {
    int id;
    String firstName;
    String lastName;
    String middleName;
    String username;
    @ToString.Exclude
    transient String password;
    UserStatus status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(status);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public boolean hasRole(Set<UserStatus> requiredRole) {
        if (status == UserStatus.ADMIN) {
            return true;
        } else if (requiredRole.size() == 0)
            return true;
        else
            for (UserStatus role : requiredRole) {
                if (role.equals(status))
                    return true;
            }
        return false;
    }
}
