package ru.spliterash.pcmasterserver.api.methods.catalog.update.componentType;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.update.DataUpdateResponse;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

import static ru.spliterash.pcmasterserver.api.methods.catalog.update.DataUpdateResponse.Response;

@Component
public class CatalogUpdateComponentType extends AbstractExecutor<CatalogUpdateComponentTypeRequest, DataUpdateResponse> {
    @Override
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataUpdateResponse execute(CatalogUpdateComponentTypeRequest json, PcMasterUser user) throws MethodExecuteException {
        if (json.getId() == null || json.getType() == null) {
            throw new MethodExecuteException(MethodExecuteException.ExceptionCode.NULL_FIELD, "Type can't be null");
        }
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source
                .addValue("id", json.getId())
                .addValue("image", json.getImageUrl())
                .addValue("type", json.getType())
                .addValue("required", json.isRequired() ? 1 : 0)
                .addValue("description", json.getDescription());
        int rows = template.update(
                "UPDATE component_type set name = :type,description = :description,image_url = :image, required = :required where id = :id",
                source,
                key,
                new String[]{"id"}
        );
        if (rows > 0)
            return new DataUpdateResponse(Response.OK);
        else
            return new DataUpdateResponse(Response.NOT_FOUND);
    }
}
