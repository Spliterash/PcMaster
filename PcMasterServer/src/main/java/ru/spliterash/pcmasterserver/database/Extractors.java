package ru.spliterash.pcmasterserver.database;

import org.springframework.jdbc.core.ResultSetExtractor;

public class Extractors {


    public static final ResultSetExtractor<Integer> firstIntExtractor = resultSet -> {
        if (resultSet.next())
            return resultSet.getInt(1);
        else
            return null;
    };
}
