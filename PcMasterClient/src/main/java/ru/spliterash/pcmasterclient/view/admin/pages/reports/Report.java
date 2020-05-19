package ru.spliterash.pcmasterclient.view.admin.pages.reports;

import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetRequest;
import ru.spliterash.pcmasterclient.api.methods.order.getAll.OrderGetAll;
import ru.spliterash.pcmasterclient.api.methods.order.getAll.OrderGetAllResponse;
import ru.spliterash.pcmasterclient.api.models.Component;
import ru.spliterash.pcmasterclient.api.models.ComponentType;
import ru.spliterash.pcmasterclient.api.models.Supplier;
import ru.spliterash.pcmasterclient.interfaces.ServerData;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Report extends HBox implements Page {
    @FXML
    private AnchorPane graphicPane;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;

    public Report() {
        Utils.loadFXML(this);
        Utils.setAnchorProperty(this);
    }

    @Override
    public void onOpen() {
        RequestManager.getExecutor(CatalogGet.class).updateCache(new CatalogGetRequest(false), this);
        RequestManager.getExecutor(OrderGetAll.class).updateCache(new CatalogGetRequest(false), this);
    }

    /**
     * @return Стрим со всеми покупками сортированный по дате
     */
    private Stream<Pair<Date, Set<Component>>> getOrdersStream() {
        OrderGetAllResponse orders = RequestManager.getExecutor(OrderGetAll.class).responseProperty().get();
        Stream<Pair<Date, Set<Component>>> stream = orders
                .getOrders()
                .stream()
                .map(OrderGetAllResponse.OrderGetRow::getOrders)
                .flatMap(Collection::stream)
                .map(o -> new Pair<>(o.getDate(), o.getFromCache().keySet()))
                .sorted(Comparator.comparing(Pair::getKey));
        LocalDate from = fromDate.getValue();
        if (from != null) {
            Instant inst = from.atStartOfDay(ZoneId.systemDefault()).toInstant();
            stream = stream.filter(k -> k.getKey().toInstant().isAfter(inst));
        }
        LocalDate to = toDate.getValue();
        if (to != null) {
            Instant inst = to.atStartOfDay(ZoneId.systemDefault()).toInstant();
            stream = stream.filter(k -> k.getKey().toInstant().isBefore(inst));
        }
        return stream;
    }

    private String getMonth(Calendar calendar) {
        return Month.of(calendar.get(Calendar.MONTH) + 1).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR);
    }

    private Map<String, List<Set<Component>>> getMonthMapSplit() {
        List<Pair<Date, Set<Component>>> list = getOrdersStream().collect(Collectors.toList());
        Map<String, List<Set<Component>>> monthMap = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        for (Pair<Date, Set<Component>> pair : list) {
            calendar.setTime(pair.getKey());
            String month = getMonth(calendar);
            List<Set<Component>> lList = monthMap.computeIfAbsent(month, k -> new ArrayList<>());
            lList.add(pair.getValue());
        }
        return monthMap;
    }

    private Map<String, Set<Component>> getMonthMap() {
        List<Pair<Date, Set<Component>>> list = getOrdersStream().collect(Collectors.toList());
        Map<String, Set<Component>> monthMap = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        for (Pair<Date, Set<Component>> pair : list) {
            calendar.setTime(pair.getKey());
            String month = getMonth(calendar);
            monthMap.merge(month, pair.getValue(), (c1, c2) -> new HashSet<Component>() {{
                addAll(c1);
                addAll(c2);
            }});
        }
        return monthMap;
    }

    private void setContent(Node node) {
        ObservableList<Node> c = graphicPane.getChildren();
        c.clear();
        Utils.setAnchorProperty(node);
        c.add(node);
    }

    private <T extends ServerData> T getFromCache(Class<T> tClass, int id) {
        try {
            Method m = tClass.getMethod("cacheFromId", int.class);
            //noinspection unchecked
            return (T) m.invoke(null, id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @SuppressWarnings("DuplicatedCode")
    private <T extends ServerData> void fillServerData(Class<T> clazz, Function<Component, Integer> getId) {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(1D);
        yAxis.setLowerBound(0D);
        int max = 0;
        yAxis.setAutoRanging(false);
        LineChart<String, Number> graphic = new LineChart<>(new CategoryAxis(), yAxis);
        Utils.setAnchorProperty(graphic);
        setContent(graphic);
        ObservableList<Series<String, Number>> gData = graphic.getData();
        Map<String, Set<Component>> map = getMonthMap();
        for (Map.Entry<String, Set<Component>> entry : map.entrySet()) {
            //Считает количество компонентов
            //Ключ ID, значение колво
            Map<Integer, Integer> count = new HashMap<>();
            for (Component component : entry.getValue()) {
                count.merge(getId.apply(component), 1, Integer::sum);
            }
            for (Map.Entry<Integer, Integer> componentTypeEntry : count.entrySet()) {
                T serverData = getFromCache(clazz, componentTypeEntry.getKey());
                if (serverData == null)
                    continue;
                int countC = componentTypeEntry.getValue();
                if (countC > max)
                    max = countC;
                Series<String, Number> s = gData
                        .stream()
                        .filter(d -> d.getName().equals(serverData.getName()))
                        .findFirst()
                        .orElseGet(() -> {
                            Series<String, Number> series = new Series<>(serverData.getName(), FXCollections.observableArrayList());
                            gData.add(series);
                            return series;
                        });
                ObservableList<XYChart.Data<String, Number>> sData = s.getData();
                XYChart.Data<String, Number> lineData = new XYChart.Data<>(entry.getKey(), componentTypeEntry.getValue());
                sData.add(lineData);
                Tooltip.install(lineData.getNode(), new Tooltip(
                        "Название: " + serverData.getName() + "\n" +
                                "Количество: " + countC)
                );
            }
        }
        yAxis.setUpperBound(max);
        setHideable(graphic);
    }

    private void setHideable(XYChart<?, ?> chart) {
        for (Node n : chart.getChildrenUnmodifiable()) {
            if (n instanceof Legend) {
                Legend l = (Legend) n;
                for (Legend.LegendItem li : l.getItems()) {
                    for (XYChart.Series<?, ?> s : chart.getData()) {
                        if (s.getName().equals(li.getText())) {
                            li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
                            li.getSymbol().setOnMouseClicked(me -> {
                                if (me.getButton() == MouseButton.PRIMARY) {
                                    s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
                                    for (XYChart.Data<?, ?> d : s.getData()) {
                                        if (d.getNode() != null) {
                                            d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
                                        }
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Показать график с типами компонентов
     */
    @FXML
    private void onClickCategory() {
        fillServerData(ComponentType.class, Component::getComponentType);
    }


    @FXML
    private void onClickSupplier() {
        fillServerData(Supplier.class, Component::getSupplier);
    }

    @SuppressWarnings("DuplicatedCode")
    @FXML
    private void onClickMonthCount() {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(1D);
        yAxis.setLowerBound(0D);
        int max = 0;
        yAxis.setAutoRanging(false);
        LineChart<String, Number> graphic = new LineChart<>(new CategoryAxis(), yAxis);
        Utils.setAnchorProperty(graphic);
        setContent(graphic);
        ObservableList<Series<String, Number>> gData = graphic.getData();
        Map<String, List<Set<Component>>> map = getMonthMapSplit();
        ObservableList<XYChart.Data<String, Number>> list = FXCollections.observableArrayList();
        Series<String, Number> series = new Series<>("Динамика продаж", list);
        gData.add(series);
        for (Map.Entry<String, List<Set<Component>>> entry : map.entrySet()) {
            int size = entry.getValue().size();
            if (size > max)
                max = size;
            XYChart.Data<String, Number> lineData = new XYChart.Data<>(
                    entry.getKey(),
                    size
            );
            list.add(lineData);
            Tooltip.install(lineData.getNode(), new Tooltip("Количество: " + size));
        }
        yAxis.setUpperBound(max);
        setHideable(graphic);
    }
}
