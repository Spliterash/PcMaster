package ru.spliterash.pcmasterclient.view.user.pages.build;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.methods.order.Order;
import ru.spliterash.pcmasterclient.api.methods.order.getMy.OrderGetMy;
import ru.spliterash.pcmasterclient.api.methods.order.getMy.OrderGetMyResponse;
import ru.spliterash.pcmasterclient.api.methods.order.make.OrderMake;
import ru.spliterash.pcmasterclient.api.methods.order.make.OrderMakeRequest;
import ru.spliterash.pcmasterclient.api.models.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BuildPageModel {
    private final BuildPage controller;

    public BuildPageModel(BuildPage controller) {
        this.controller = controller;

        RequestManager.getExecutor(CatalogGet.class).responseProperty().addListener(this::updateCatalog);
    }

    public void updateCatalog(ObservableValue<? extends CatalogGetResponse> observable, CatalogGetResponse oldValue, CatalogGetResponse newValue) {
        if (newValue != null)
            controller.updateTypes(newValue.getComponentTypes());
        else
            controller.updateTypes(null);
    }

    public void makePurchase(Set<Component> set) {
        OrderMake executor = RequestManager.getExecutor(OrderMake.class);
        OrderMakeRequest request = new OrderMakeRequest(
                set
                        .stream()
                        .map(Component::getId)
                        .collect(Collectors.toList())
        );
        executor.executeSync(request, (orderMakeResponse, throwable) -> {
            if (orderMakeResponse != null) {
                int id = orderMakeResponse.getOrderId();
                OrderGetMyResponse cache = RequestManager.getExecutor(OrderGetMy.class).responseProperty().get();
                //Если есть локальные данные
                if (cache != null) {
                    Map<Integer, Integer> components = new HashMap<>();
                    for (Component component : set) {
                        components.put(component.getId(), component.getPrice());
                    }
                    Order order = new Order(id, new Date(), components);
                    cache.getOrders().add(order);
                }
            } else if (throwable instanceof MethodExecuteException) {
                MethodExecuteException ex = (MethodExecuteException) throwable;
                if (ex.getCode().equals(MethodExecuteException.ExceptionCode.METHOD_EXCEPTION)) {
                    RequestManager.getExecutor(CatalogGet.class).updateCacheNow(new CatalogGetRequest(true), controller);
                    Utils.showWaitAlert(
                            Alert.AlertType.WARNING,
                            "Сервер говорит что недостаёт компонентов",
                            "Отсутсвует необходимый компонент",
                            "Похоже пока вы делали заказ, администратор сайта добавил новую категорию, мы сейчас же загрузим её"
                    );
                }else{
                    Utils.showServerError(ex);
                }
            }
        }, controller);
    }
}
