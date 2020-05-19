package ru.spliterash.pcmasterserver.api.methods.catalog.add.component;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.add.DataAddResponse;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

@Component
public class CatalogAddComponent extends AbstractExecutor<CatalogAddComponentRequest, DataAddResponse> {
    @Override
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataAddResponse execute(CatalogAddComponentRequest json, PcMasterUser user) {
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source
                .addValue("name", json.getName())
                .addValue("price", json.getPrice())
                .addValue("supplier", json.getSupplierId())
                .addValue("image", json.getImageUrl())
                .addValue("type", json.getTypeId());
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        template.update(
                "INSERT INTO component (name, price, component_type_id, supplier_id,image_url)" +
                        " VALUES (" +
                        ":name," +
                        ":price," +
                        ":type," +
                        ":supplier," +
                        ":image)",
                source,
                key,
                new String[]{"id"}
        );
        Number keyObj = key.getKey();
        if (keyObj != null) {
            return new DataAddResponse(DataAddResponse.Response.OK, keyObj.intValue());
        } else {
            return new DataAddResponse(DataAddResponse.Response.EXISTS, -1);
        }
    }
}
