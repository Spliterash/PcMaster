package ru.spliterash.pcmasterserver.api.methods.catalog.delete.componentType;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

import static ru.spliterash.pcmasterserver.api.methods.catalog.delete.DataDeleteResponse.Response.ERROR;
import static ru.spliterash.pcmasterserver.api.methods.catalog.delete.DataDeleteResponse.Response.OK;

@Component
public class CatalogDeleteComponentType extends AbstractExecutor<CatalogDeleteComponentTypeRequest, DataDeleteResponse> {
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataDeleteResponse execute(CatalogDeleteComponentTypeRequest json, PcMasterUser user) {
        JdbcTemplate template = getQueries().getTemplate().getJdbcTemplate();
        int count = template.update("DELETE IGNORE FROM component_type where id = ?", json.getId());
        return new DataDeleteResponse(count > 0 ? OK : ERROR);
    }
}
