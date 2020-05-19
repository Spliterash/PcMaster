package ru.spliterash.pcmasterserver.api.methods.order.make;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderMakeRequest {
    public List<Integer> components;

}
