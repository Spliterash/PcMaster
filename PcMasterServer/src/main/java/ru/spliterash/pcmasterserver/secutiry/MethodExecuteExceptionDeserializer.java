package ru.spliterash.pcmasterserver.secutiry;


import com.google.gson.*;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;

import java.lang.reflect.Type;

public class MethodExecuteExceptionDeserializer implements JsonSerializer<MethodExecuteException> {
    @Override
    public JsonElement serialize(MethodExecuteException e, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.add("code", new JsonPrimitive(e.getCode().name()));
        object.add("error", new JsonPrimitive(e.getError()));
        if (e.getOther().length > 0) {
            JsonArray array = new JsonArray(e.getOther().length);
            for (String s : e.getOther()) {
                array.add(s);
            }
            object.add("other", array);
        }
        return object;
    }
}
