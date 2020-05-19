package ru.spliterash.pcmasterserver.api.methods.catalog.add.componentType;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.add.DataAddResponse;
import ru.spliterash.pcmasterserver.database.Extractors;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

@Component
public class CatalogAddComponentType extends AbstractExecutor<CatalogAddComponentTypeRequest, DataAddResponse> {
    @Override
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataAddResponse execute(CatalogAddComponentTypeRequest json, PcMasterUser user) throws MethodExecuteException {
        if (json.getType() == null) {
            throw new MethodExecuteException(MethodExecuteException.ExceptionCode.NULL_FIELD, "Type can't be null");
        }
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source
                .addValue("type", json.getType())
                .addValue("image", json.getImageUrl())
                .addValue("required", json.isRequired() ? 1 : 0)
                .addValue("description", json.getDescription());
        template.update(
                "INSERT IGNORE INTO component_type (name,description,image_url,required) values (:type,:description,:image,:required)",
                source,
                key,
                new String[]{"id"}
        );
        Number numba = key.getKey();
        if (numba != null)
            return new DataAddResponse(DataAddResponse.Response.OK, numba.intValue());
        else {
            Integer id = template.query("SELECT id from component_type where name = :type", source, Extractors.firstIntExtractor);
            return new DataAddResponse(DataAddResponse.Response.EXISTS, id);
        }
    }
}
