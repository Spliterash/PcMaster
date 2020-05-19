package ru.spliterash.pcmasterclient.api.methods.order;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.Value;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.models.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Value
public class Order {
    int id;
    Date date;
    //Компоненты и их цена
    @Setter(AccessLevel.NONE)
    Map<Integer, Integer> components;

    public Map<Component, Integer> getFromCache() {
        Map<Component, Integer> map = new LinkedHashMap<>();
        CatalogGetResponse response = RequestManager.getExecutor(CatalogGet.class).responseProperty().get();
        if (response == null)
            return map;
        for (Map.Entry<Integer, Integer> entry : components.entrySet()) {
            response
                    .getComponents()
                    .stream()
                    .filter(c -> c.getId() == entry.getKey())
                    .findFirst()
                    .ifPresent(c -> map.put(c, entry.getValue()));
        }
        return map;
    }

    public int getSum() {
        return components
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public String toString() {
        return "Заказ от " + Utils.dateToString(date) + " на сумму " + getSum();
    }
}
