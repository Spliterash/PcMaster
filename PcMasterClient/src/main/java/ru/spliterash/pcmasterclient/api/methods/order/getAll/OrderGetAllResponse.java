package ru.spliterash.pcmasterclient.api.methods.order.getAll;

import lombok.Getter;
import lombok.Value;
import ru.spliterash.pcmasterclient.api.methods.order.Order;
import ru.spliterash.pcmasterclient.api.models.PcMasterUser;

import java.util.List;

@Value
public class OrderGetAllResponse {

    List<OrderGetRow> orders;

    @Getter
    public static class OrderGetRow {
        private PcMasterUser user;
        private List<Order> orders;
    }
}
