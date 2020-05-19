package ru.spliterash.pcmasterserver.api.methods.order.make;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderMake extends AbstractExecutor<OrderMakeRequest, OrderMakeResponse> {

    @Override
    public OrderMakeResponse execute(OrderMakeRequest json, PcMasterUser user) throws MethodExecuteException {
        if (json.getComponents() == null) {
            throw new MethodExecuteException(MethodExecuteException.ExceptionCode.NULL_FIELD, "Nulled collection");
        }
        NamedParameterJdbcTemplate queries = getQueries().getTemplate();
        List<Integer> missed = queries.query(
                "SELECT \n" +
                        "    ct.id\n" +
                        "FROM\n" +
                        "    component_type ct\n" +
                        "WHERE\n" +
                        "    ct.required = 1\n" +
                        "        AND ct.id NOT IN (SELECT \n" +
                        "            t.id\n" +
                        "        FROM\n" +
                        "            `component` c\n" +
                        "                JOIN\n" +
                        "            `component_type` t ON c.component_type_id = t.id\n" +
                        "        WHERE\n" +
                        "            c.id IN (:components) AND t.required = 1)", new MapSqlParameterSource("components", json.getComponents()), resultSet -> {
                    List<Integer> list = new ArrayList<>();
                    while (resultSet.next()) {
                        list.add(resultSet.getInt(1));
                    }
                    return list;
                });
        if (missed == null)
            throw new IllegalArgumentException("Impossible exception");
        //Отсутствуют необходимые компоненты
        if (missed.size() > 0) {
            String[] requiredStr = missed
                    .stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
            throw new MethodExecuteException(MethodExecuteException.ExceptionCode.METHOD_EXCEPTION, "Missing required components", requiredStr);
        }
        //Создаём новый заказ
        MapSqlParameterSource source = new MapSqlParameterSource();
        source.addValue("client_id", user.getId());
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        queries.update("INSERT INTO `order` (client_id) values (:client_id)", source, key, new String[]{"id"});
        //noinspection ConstantConditions отрубает подсветку на нулл, его тут не может быть тупо
        int orderId = key.getKey().intValue();
        //Карта id компонента и его цена
        Map<Integer, Integer> priceMap = queries.query("SELECT id,price from component where id in (:id)",
                new MapSqlParameterSource("id", json.getComponents()),
                resultSet -> {
                    Map<Integer, Integer> map = new HashMap<>();
                    while (resultSet.next()) {
                        map.put(resultSet.getInt(1), resultSet.getInt(2));
                    }
                    return map;
                });
        if (priceMap == null)
            throw new IllegalArgumentException("Impossible again");
        MapSqlParameterSource[] maps = new MapSqlParameterSource[priceMap.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : priceMap.entrySet()) {
            maps[i++] = new MapSqlParameterSource()
                    .addValue("order", orderId)
                    .addValue("component", entry.getKey())
                    .addValue("price", entry.getValue());
        }
        queries.batchUpdate("INSERT INTO order_component (order_id, component_id, price) values (:order,:component,:price)", maps);
        return new OrderMakeResponse(orderId);
    }
}
