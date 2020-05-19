package ru.spliterash.pcmasterserver.api.methods.catalog.update.supplier;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.update.DataUpdateResponse;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

@Component
public class CatalogUpdateSupplier extends AbstractExecutor<CatalogUpdateSupplierRequest, DataUpdateResponse> {

    @Override
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataUpdateResponse execute(CatalogUpdateSupplierRequest json, PcMasterUser user) throws MethodExecuteException {
        if (json.getSupplier() == null) {
            throw new MethodExecuteException(MethodExecuteException.ExceptionCode.NULL_FIELD, "Type can't be null");
        }
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        MapSqlParameterSource source = new MapSqlParameterSource();
        source
                .addValue("id", json.getId())
                .addValue("supplier", json.getSupplier())
                .addValue("image", json.getImageUrl());
        int rows = template.update(
                "UPDATE supplier set name = :supplier, image_url = :image where id = :id",
                source
        );
        if (rows > 0)
            return new DataUpdateResponse(DataUpdateResponse.Response.OK);
        else
            return new DataUpdateResponse(DataUpdateResponse.Response.NOT_FOUND);

    }
}
