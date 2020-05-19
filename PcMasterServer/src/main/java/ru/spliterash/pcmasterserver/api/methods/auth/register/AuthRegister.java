package ru.spliterash.pcmasterserver.api.methods.auth.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.auth.register.AuthRegisterResponse.Response;
import ru.spliterash.pcmasterserver.database.Extractors;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
@Component
public class AuthRegister extends AbstractExecutor<AuthRegisterRequest, AuthRegisterResponse> {
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public AuthRegisterResponse execute(AuthRegisterRequest json, PcMasterUser user) {
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("user", json.getUsername());
        Integer rows = template.query("SELECT count(*) FROM account where username = :user", source, Extractors.firstIntExtractor);
        if (rows == null || rows == 0) {
            source
                    .addValue("first_name", json.getFirstName())
                    .addValue("last_name", json.getLastName())
                    .addValue("middle_name", json.getMiddleName())
                    .addValue("password", encoder.encode(json.getPassword()));
            template.update(
                    "INSERT INTO account (first_name, last_name, middle_name, username, password) " +
                            "VALUES (:first_name,:last_name,:middle_name,:user,:password)", source);
            return new AuthRegisterResponse(Response.OK);
        } else {
            return new AuthRegisterResponse(Response.USERNAME_TAKEN);
        }
    }

    @Override
    public boolean authRequired() {
        return false;
    }
}
