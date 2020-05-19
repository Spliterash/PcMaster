package ru.spliterash.pcmasterserver.api.methods.auth.login;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.Utils;

@Component
public class AuthLogin extends AbstractExecutor<AuthLoginRequest, AuthLoginResponse> {

    private final BCryptPasswordEncoder encoder;

    public AuthLogin(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public AuthLoginResponse execute(AuthLoginRequest json, PcMasterUser alwaysNulled) throws MethodExecuteException {
        if (json.getLogin() == null || json.getPassword() == null)
            throw new MethodExecuteException(MethodExecuteException.ExceptionCode.NULL_FIELD, "Values are nulled");
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("username", json.getLogin());
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        String pass = template
                .query("SELECT password from account where username = :username",
                        source, resultSet -> {
                            if (resultSet.next())
                                return resultSet.getString("password");
                            else
                                return null;
                        });
        if (pass == null)
            return new AuthLoginResponse(null);
        else if(encoder.matches(json.getPassword(),pass)){
            String token = Utils.generateNewToken();
            source.addValue("token", token);
            template.update("UPDATE account set token = :token where username = :username", source);
            return new AuthLoginResponse(token);
        }else
            return new AuthLoginResponse(null);
    }

    @Override
    public boolean authRequired() {
        return false;
    }
}
