package ru.spliterash.pcmasterclient.api.methods.order.make;

import lombok.Value;

import java.util.List;

@Value
public class OrderMakeRequest {
    public List<Integer> components;

}
