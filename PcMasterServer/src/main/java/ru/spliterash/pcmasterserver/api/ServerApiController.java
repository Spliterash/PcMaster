package ru.spliterash.pcmasterserver.api;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.logging.Logger;

@Controller
@RequestMapping("/api")
public class ServerApiController {
    private final Set<AbstractExecutor<?, ?>> methods;

    public ServerApiController(Set<AbstractExecutor<?, ?>> methods) {
        this.methods = methods;
    }

    @ResponseBody
    @ExceptionHandler(value = {
            MalformedJsonException.class,
            JsonParseException.class,
            JsonSyntaxException.class,
            JsonMappingException.class
    }
    )
    public Object onParseException(Exception ex) {
        return new MethodExecuteException(MethodExecuteException.ExceptionCode.JSON_SYNTAX, "Can't parse json " + ex.getLocalizedMessage());
    }

    @ResponseBody
    @PostMapping(
            value = "/{method}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Object onPost(@PathVariable String method, @AuthenticationPrincipal PcMasterUser user, @RequestBody JsonObject json, HttpServletResponse httpResponse) {
        Logger logger = Logger.getGlobal();
        logger.info("====================" + method + "====================");
        logger.info("Request: " + json.toString());
        logger.info("User: " + user);
        AbstractExecutor<?, ?> methodObj = methods
                .stream()
                .filter(m -> m.getMethodName().equalsIgnoreCase(method))
                .findFirst()
                .orElse(null);
        if (methodObj != null) {
            try {
                long c = System.currentTimeMillis();
                Object response = methodObj.checkAuth(json, user ,httpResponse);
                System.out.println("Time: " + (System.currentTimeMillis() - c));
                return response;
            } catch (MethodExecuteException e) {
                return e;
            } catch (Exception ex) {
                ex.printStackTrace();
                return ex;
            }
        } else {
            return new MethodExecuteException(MethodExecuteException.ExceptionCode.METHOD_NOT_FOUND, "Method " + method + " not found");
        }
    }
}
 