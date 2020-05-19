package ru.spliterash.pcmasterserver.api.methods.order.getAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.MethodExecuteException;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.methods.order.Order;
import ru.spliterash.pcmasterserver.secutiry.PcMasterDetailsService;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.UserStatus;

import java.util.*;

@Component
public class OrderGetAll extends AbstractExecutor<Object, OrderGetAllResponse> {
    @Autowired
    private PcMasterDetailsService service;

    @Override
    public Set<UserStatus> requiredRoles() {
        return Collections.singleton(UserStatus.ADMIN);
    }

    @Override
    public OrderGetAllResponse execute(Object json, PcMasterUser user) throws MethodExecuteException {
        NamedParameterJdbcTemplate template = getQueries().getTemplate();
        List<OrderGetAllResponse.OrderGetRow> orders = new ArrayList<>();
        //Подготавливаем заказы и клиентов которые их купили
        {
            SqlRowSet set = template.getJdbcTemplate().queryForRowSet("SELECT id, client_id, order_time from `order` order by id DESC");
            while (set.next()) {
                int clientId = set.getInt("client_id");
                //Ищем список, если нет создаём новый
                List<Order> list = orders
                        .stream()
                        .filter(e -> e.getUser().getId() == clientId)
                        .map(OrderGetAllResponse.OrderGetRow::getOrders)
                        .findFirst()
                        .orElseGet(() -> {
                            PcMasterUser pUser = service.loadUserById(clientId);
                            OrderGetAllResponse.OrderGetRow row = new OrderGetAllResponse.OrderGetRow(pUser);
                            orders.add(row);
                            return row.getOrders();
                        });
                list.add(new Order(
                        set.getInt("id"),
                        set.getTimestamp("order_time")
                ));
            }
        }
        //Заполняем
        {
            SqlRowSet set = template.getJdbcTemplate().queryForRowSet(
                    "SELECT o.client_id,oc.order_id, oc.component_id, oc.price " +
                            "from order_component oc join `order` o on o.id = oc.order_id");
            while (set.next()) {
                int client = set.getInt("client_id");
                int orderId = set.getInt("order_id");
                List<Order> list = orders
                        .stream()
                        .filter(p -> p.getUser().getId() == client)
                        .map(OrderGetAllResponse.OrderGetRow::getOrders)
                        .findFirst()
                        .orElse(null);
                if (list == null)
                    continue;
                Order order = list
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
        return new OrderGetAllResponse(orders);
    }
}
