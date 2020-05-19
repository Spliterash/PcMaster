package ru.spliterash.pcmasterserver.api.methods.catalog.update.component;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.update.DataUpdateResponse;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

@Component
public class CatalogUpdateComponent extends AbstractExecutor<CatalogUpdateComponentRequest, DataUpdateResponse> {
    @Override
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataUpdateResponse execute(CatalogUpdateComponentRequest json, PcMasterUser user) {
        MapSqlParameterSource source = new MapSqlParameterSource();
        source
                .addValue("id", json.getId())
                .addValue("name", json.getName())
                .addValue("price", json.getPrice())
                .addValue("supplier", json.getSupplierId())
                .addValue("image", json.getImageUrl())
                .addValue("type", json.getTypeId());
        int rows = getQueries()
                .getTemplate()
                .update(
                        "UPDATE component set " +
                                "name = :name, " +
                                "price = :price, " +
                                "supplier_id = :supplier," +
                                "image_url = :image," +
                                "component_type_id = :type where id = :id",
                        source);
        if (rows > 0) {
            return new DataUpdateResponse(DataUpdateResponse.Response.OK);
        } else
            return new DataUpdateResponse(DataUpdateResponse.Response.NOT_FOUND);
    }
}
