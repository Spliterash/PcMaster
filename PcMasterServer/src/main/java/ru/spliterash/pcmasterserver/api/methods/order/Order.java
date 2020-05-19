package ru.spliterash.pcmasterserver.api.methods.order;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Order {
    private final int id;
    private final Date date;
    //Компоненты и их цена
    @Setter(AccessLevel.NONE)
    private Map<Integer, Integer> components = new LinkedHashMap<>();
}
