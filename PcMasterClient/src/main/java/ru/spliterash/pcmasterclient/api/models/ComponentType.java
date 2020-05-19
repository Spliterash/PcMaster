package ru.spliterash.pcmasterclient.api.models;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.DataAddResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.componentType.CatalogAddComponentType;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.componentType.CatalogAddComponentTypeRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.componentType.CatalogDeleteComponentType;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.componentType.CatalogDeleteComponentTypeRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.DataUpdateResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.componentType.CatalogUpdateComponentType;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.componentType.CatalogUpdateComponentTypeRequest;
import ru.spliterash.pcmasterclient.interfaces.ServerData;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ComponentType implements ServerData {
    @Getter
    private int id;
    private StringProperty name;
    @SerializedName("image_url")
    private StringProperty imageUrl;
    private StringProperty description;
    private BooleanProperty required;

    public ComponentType() {

    }

    public ComponentType(int id, String name, String imageUrl, String description, boolean required) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.imageUrl = new SimpleStringProperty(imageUrl);
        this.description = new SimpleStringProperty(description);
        this.required = new SimpleBooleanProperty(required);
    }

    public static ComponentType cacheFromId(int id) {
        CatalogGetResponse r = RequestManager
                .getExecutor(CatalogGet.class)
                .responseProperty()
                .get();
        if (r != null) {
            return r
                    .getComponentTypes()
                    .stream()
                    .filter(c -> c.getId() == id)
                    .findFirst()
                    .orElse(null);
        } else
            return null;
    }


    public boolean isRequired() {
        return required.get();
    }

    public BooleanProperty requiredProperty() {
        return required;
    }

    @Override
    public boolean isSimilar(ServerData another) {
        if (this == another)
            return true;
        if (another instanceof ComponentType) {
            ComponentType type = (ComponentType) another;
            return getId() == type.getId() &&
                    Objects.equals(getName(), type.getName()) &&
                    Objects.equals(getImageUrl(), type.getImageUrl()) &&
                    Objects.equals(getDescription(), type.getDescription()) &&
                    isRequired() == type.isRequired();
        } else
            return false;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ComponentType createCopy() {
        ComponentType clone = new ComponentType();
        clone.id = getId();
        clone.name = new SimpleStringProperty(getName());
        clone.description = new SimpleStringProperty(getDescription());
        clone.imageUrl = new SimpleStringProperty(getImageUrl());
        clone.required = new SimpleBooleanProperty(isRequired());
        return clone;
    }

    @Override
    public DataAddResponse add() throws IOException, MethodExecuteException {
        DataAddResponse response = RequestManager.getInstance().getExecutor(CatalogAddComponentType.class)
                .execute(
                        new CatalogAddComponentTypeRequest(
                                getName(),
                                getDescription(),
                                getImageUrl(),
                                isRequired()
                        )
                );
        id = response.getId();
        return response;
    }

    @Override
    public DataUpdateResponse update() throws IOException, MethodExecuteException {
        if (getId() == -1) {
            throw new IllegalArgumentException("Not server data");
        }
        return RequestManager.getInstance().getExecutor(CatalogUpdateComponentType.class)
                .execute(
                        new CatalogUpdateComponentTypeRequest(
                                id,
                                getName(),
                                getDescription(),
                                getImageUrl(),
                                isRequired()
                        )
                );
    }

    @Override
    public DataDeleteResponse delete() throws IOException, MethodExecuteException {
        if (getId() == -1) {
            throw new IllegalArgumentException("Not server data");
        }
        return RequestManager.getInstance().getExecutor(CatalogDeleteComponentType.class)
                .execute(
                        new CatalogDeleteComponentTypeRequest(
                                getId()
                        )
                );
    }

    /**
     * Найтупейшая имплементация
     * Берёт из кеша компоненты
     */
    public List<Component> getComponentsFromCache() {
        CatalogGetResponse lastResponse = RequestManager
                .getInstance()
                .getExecutor(CatalogGet.class)
                .responseProperty()
                .get();
        if (lastResponse != null) {
            return lastResponse
                    .getComponents()
                    .stream()
                    .filter(c -> c.getComponentType() == getId())
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
