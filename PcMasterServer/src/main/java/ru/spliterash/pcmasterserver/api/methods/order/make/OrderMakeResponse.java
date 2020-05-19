package ru.spliterash.pcmasterserver.api.methods.order.make;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class OrderMakeResponse {
    @SerializedName("order_id")
    int orderId;
}
