package ru.spliterash.pcmasterserver.secutiry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.database.PreparedQueries;

@Component
public class PcMasterUserToken {
    private final PreparedQueries q;
    private final PcMasterDetailsService service;

    public PcMasterUserToken(PreparedQueries q, PcMasterDetailsService service) {
        this.q = q;
        this.service = service;
    }


    public PcMasterUser retrieveUser(String token) throws AuthenticationException {
        if (token != null) {
            JdbcTemplate template = q.getTemplate().getJdbcTemplate();
            String username = template.query("SELECT username from account where token = ?", resultSet -> {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                } else
                    return null;
            }, token);
            if (username == null) {
                throw new UsernameNotFoundException("Cannot find user with authentication token=" + token);
            } else {
                return service.loadUserByUsername(username);
            }
        } else {
            throw new UsernameNotFoundException("Token is null");
        }
    }
}
