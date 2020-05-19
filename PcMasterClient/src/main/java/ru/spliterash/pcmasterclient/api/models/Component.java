package ru.spliterash.pcmasterclient.api.models;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.DataAddResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.component.CatalogAddComponent;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.component.CatalogAddComponentRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.component.CatalogDeleteComponent;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.component.CatalogDeleteComponentRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.DataUpdateResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.component.CatalogUpdateComponent;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.component.CatalogUpdateComponentRequest;
import ru.spliterash.pcmasterclient.interfaces.ServerData;

import java.io.IOException;
import java.util.Objects;

public class Component implements ServerData {
    private int id;
    private StringProperty name;
    private IntegerProperty supplier;
    private IntegerProperty price;
    private StringProperty image;

    public Component() {
    }

    public Component(int id, String name, int supplier, int price, String image, int componentType) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.supplier = new SimpleIntegerProperty(supplier);
        this.price = new SimpleIntegerProperty(price);
        this.image = new SimpleStringProperty(image);
        this.componentType = new SimpleIntegerProperty(componentType);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getSupplier() {
        return supplier.get();
    }

    public IntegerProperty supplierProperty() {
        return supplier;
    }

    public int getPrice() {
        return price.get();
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public String getImage() {
        return image.get();
    }

    public StringProperty imageProperty() {
        return image;
    }

    public int getComponentType() {
        return componentType.get();
    }

    public IntegerProperty componentTypeProperty() {
        return componentType;
    }

    @SerializedName("component_type")
    private IntegerProperty componentType;


    /**
     * Возращает тип компонента из закешированного ответа
     * Null если кеша нет или не может найти id (хотя тут по хорошему вообще эксепшн выбросить)
     */
    public ComponentType getTypeFromCache() {
        return ComponentType.cacheFromId(getComponentType());
    }

    /**
     * Возращает поставщика из закешированного ответа
     * Опять же null если не найдено или нет кеша
     */
    public Supplier getSupplierFromCache() {
        return Supplier.cacheFromId(getSupplier());
    }

    @Override
    public boolean isSimilar(ServerData o) {
        if (this == o) return true;
        if (!(o instanceof Component)) return false;
        Component component = (Component) o;
        return getId() == component.getId() &&
                Objects.equals(getName(), component.getName()) &&
                Objects.equals(getSupplier(), component.getSupplier()) &&
                Objects.equals(getPrice(), component.getPrice()) &&
                Objects.equals(getImage(), component.getImage()) &&
                Objects.equals(getComponentType(), component.getComponentType());
    }

    @Override
    public Component createCopy() {
        Component clone = new Component();
        clone.id = getId();
        clone.image = new SimpleStringProperty(getImage());
        clone.supplier = new SimpleIntegerProperty(getSupplier());
        clone.componentType = new SimpleIntegerProperty(getComponentType());
        clone.name = new SimpleStringProperty(getName());
        clone.price = new SimpleIntegerProperty(getPrice());
        return clone;
    }

    @Override
    public DataAddResponse add() throws IOException, MethodExecuteException {
        DataAddResponse response = RequestManager.getExecutor(CatalogAddComponent.class).execute(
                new CatalogAddComponentRequest(
                        getName(),
                        getPrice(),
                        getSupplier(),
                        getComponentType(),
                        getImage()
                )
        );
        id = response.getId();
        return response;
    }

    @Override
    public DataUpdateResponse update() throws IOException, MethodExecuteException {
        return RequestManager.getExecutor(CatalogUpdateComponent.class).execute(
                new CatalogUpdateComponentRequest(
                        getId(),
                        getName(),
                        getPrice(),
                        getSupplier(),
                        getComponentType(),
                        getImage()
                )
        );
    }

    @Override
    public DataDeleteResponse delete() throws IOException, MethodExecuteException {
        return RequestManager.getExecutor(CatalogDeleteComponent.class).execute(
                new CatalogDeleteComponentRequest(
                        getId()
                )
        );
    }
}
