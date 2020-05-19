package ru.spliterash.pcmasterserver.api.methods.order.getMy;

import lombok.Value;
import ru.spliterash.pcmasterserver.api.methods.order.Order;

import java.util.List;

@Value
public class OrderGetMyResponse {
    List<Order> orders;
}
