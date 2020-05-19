package ru.spliterash.pcmasterclient.api.methods.order.getMy;

import javafx.beans.property.ListProperty;
import lombok.Value;
import ru.spliterash.pcmasterclient.api.methods.order.Order;

@Value
public class OrderGetMyResponse {
    ListProperty<Order> orders;
}
