package ru.spliterash.pcmasterserver.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PreparedQueries {
    @Autowired
    private NamedParameterJdbcTemplate template;

    public NamedParameterJdbcTemplate getTemplate() {
        return template;
    }
}
