package ru.spliterash.pcmasterserver.api.methods.order.getAll;

import lombok.Getter;
import lombok.Value;
import ru.spliterash.pcmasterserver.api.methods.order.Order;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;

import java.util.ArrayList;
import java.util.List;

@Value
public class OrderGetAllResponse {
    List<OrderGetRow> orders;

    @Value
    public static class OrderGetRow {
        PcMasterUser user;
        List<Order> orders = new ArrayList<>();
    }
}
