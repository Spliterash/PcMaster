package ru.spliterash.pcmasterserver.api.methods;

import com.google.gson.*;
import org.springframework.http.HttpStatus;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.database.PreparedQueries;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractExecutor<Request, Response> {
    @Autowired
    private PreparedQueries queries;


    protected PreparedQueries getQueries() {
        return queries;
    }

    public Response execute(Request json) throws MethodExecuteException {
        return execute(json, null);
    }

    public boolean authRequired() {
        return true;
    }

    public Set<UserStatus> requiredRoles() {
        return Collections.emptySet();
    }

    public Object checkAuth(JsonObject json, PcMasterUser user, HttpServletResponse response) throws MethodExecuteException {
        Request parsed;
        try {
            parsed = getJson(json);
        } catch (Exception ex) {
            return ex;
        }
        if (authRequired()) {
            if (user == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                throw new MethodExecuteException(
                        MethodExecuteException.ExceptionCode.UNAUTHORIZED,
                        "This method need authorization");
            } else if (!user.hasRole(requiredRoles())) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                throw new MethodExecuteException(
                        MethodExecuteException.ExceptionCode.FORBIDDEN,
                        "Your account does not have permissions");
            }
        }
        return execute(parsed, user);
    }

    public abstract Response execute(Request json, PcMasterUser user) throws MethodExecuteException;

    public String getMethodName() {
        String fullName = getClass().getPackage().getName().toLowerCase();

        return (fullName.substring(index));
    }

    @SuppressWarnings("unchecked")
    private Request getJson(JsonElement json) {
        Class<Request> clazz = (Class<Request>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return gson.fromJson(json, clazz);
    }

    private final static int index = "ru.spliterash.pcmasterserver.api.methods.".length();

    @Autowired
    private Gson gson;


}
