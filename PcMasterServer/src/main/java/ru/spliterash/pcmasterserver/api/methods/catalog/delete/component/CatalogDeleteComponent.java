package ru.spliterash.pcmasterserver.api.methods.catalog.delete.component;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.Collections;
import java.util.Set;

import static ru.spliterash.pcmasterserver.api.methods.catalog.delete.DataDeleteResponse.Response.NO_EXISTS;
import static ru.spliterash.pcmasterserver.api.methods.catalog.delete.DataDeleteResponse.Response.OK;

@Component
public class CatalogDeleteComponent extends AbstractExecutor<CatalogDeleteComponentRequest, DataDeleteResponse> {
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public DataDeleteResponse execute(CatalogDeleteComponentRequest json, PcMasterUser user) {
        JdbcTemplate template = getQueries().getTemplate().getJdbcTemplate();
        int count = template.update("DELETE FROM component where id = ?", json.getId());
        return new DataDeleteResponse(count > 0 ? OK : NO_EXISTS);
    }
}
