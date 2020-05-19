package ru.spliterash.pcmasterserver.api.methods.order.getMy;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.order.Order;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderGetMy extends AbstractExecutor<Object, OrderGetMyResponse> {
    @Override
    public OrderGetMyResponse execute(Object json, PcMasterUser user) {
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        List<Order> orders = new ArrayList<>();
        //Подготавливаем список заказов
        {
            SqlRowSet set = template.getJdbcTemplate().queryForRowSet(
                    "SELECT id, order_time from `order` where client_id = ? order by id DESC",
                    user.getId()
            );
            while (set.next()) {
                orders.add(new Order(
                        set.getInt("id"),
                        set.getTimestamp("order_time")
                ));
            }
        }

        //Заполняем
        if (orders.size() > 0) {
            SqlRowSet set = template.queryForRowSet(
                    "SELECT order_id, component_id, oc.price " +
                            "from order_component oc " +
                            "join component c on oc.component_id = c.id " +
                            "join component_type ct on c.component_type_id = ct.id " +
                            "where order_id in (:orders) order by ct.id",
                    new MapSqlParameterSource("orders", orders.stream().map(Order::getId).collect(Collectors.toList()))
            );
            while (set.next()) {
                int orderId = set.getInt("order_id");
                Order order = orders
                        .stream()
                        .filter(s -> s.getId() == orderId)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("-_-"));
                order.getComponents().put(
                        set.getInt("component_id"),
                        set.getInt("price")
                );
            }
        }
        return new OrderGetMyResponse(orders);
    }
}
