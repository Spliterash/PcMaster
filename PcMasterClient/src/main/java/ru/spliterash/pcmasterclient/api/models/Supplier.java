package ru.spliterash.pcmasterclient.api.models;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.DataAddResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.supplier.CatalogAddSupplier;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.supplier.CatalogAddSupplierRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.supplier.CatalogDeleteSupplier;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.supplier.CatalogDeleteSupplierRequest;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGet;
import ru.spliterash.pcmasterclient.api.methods.catalog.get.CatalogGetResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.DataUpdateResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.supplier.CatalogUpdateSupplier;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.supplier.CatalogUpdateSupplierRequest;
import ru.spliterash.pcmasterclient.interfaces.ServerData;

import java.io.IOException;
import java.util.Objects;

public class Supplier implements ServerData {
    @Getter
    private int id;
    private StringProperty name;
    @SerializedName("image_url")
    private StringProperty imageUrl;

    public Supplier() {
    }

    public Supplier(int id, String name, String imageUrl) {
        this.id = id;
        this.name = new SimpleStringProperty(name);
        this.imageUrl = new SimpleStringProperty(imageUrl);
    }

    public static Supplier cacheFromId(int id) {
        CatalogGetResponse response = RequestManager.getExecutor(CatalogGet.class).responseProperty().get();
        if (response != null)
            return
                    response
                            .getSuppliers()
                            .stream()
                            .filter(s -> s.getId() == id)
                            .findFirst()
                            .orElse(null);
        else
            return null;
    }

    @Override
    public boolean isSimilar(ServerData o) {
        if (this == o) return true;
        if (!(o instanceof Supplier)) return false;
        Supplier supplier = (Supplier) o;
        return getId() == supplier.getId() &&
                Objects.equals(getName(), supplier.getName()) &&
                Objects.equals(getImageUrl(), supplier.getImageUrl());
    }

    public String getName() {
        return name.get();
    }

    @Override
    public ServerData createCopy() {

        Supplier cloned = new Supplier();
        cloned.id = getId();
        cloned.name = new SimpleStringProperty(getName());
        cloned.imageUrl = new SimpleStringProperty(getImageUrl());
        return cloned;
    }

    @Override
    public DataAddResponse add() throws IOException, MethodExecuteException {
        DataAddResponse response = RequestManager.getInstance().getExecutor(CatalogAddSupplier.class).execute(
                new CatalogAddSupplierRequest(
                        getName(),
                        getImageUrl()
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
        return RequestManager.getInstance().getExecutor(CatalogUpdateSupplier.class).execute(
                new CatalogUpdateSupplierRequest(
                        getId(),
                        getName(),
                        getImageUrl()
                )
        );
    }

    @Override
    public DataDeleteResponse delete() throws IOException, MethodExecuteException {
        if (getId() == -1) {
            throw new IllegalArgumentException("Not server data");
        }
        return RequestManager.getInstance().getExecutor(CatalogDeleteSupplier.class).execute(
                new CatalogDeleteSupplierRequest(
                        getId()
                )
        );
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
}
