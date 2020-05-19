package ru.spliterash.pcmasterclient.view.admin;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.util.Callback;
import lombok.Getter;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterclient.interfaces.CRUDController;
import ru.spliterash.pcmasterclient.interfaces.ServerData;
import ru.spliterash.pcmasterclient.view.other.InformativeLoad;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Getter
public abstract class CRUDModel<T extends ServerData> {
    /**
     * То что находиться на сервере
     */
    private ListProperty<T> serverData = new SimpleListProperty<>();
    private ObservableList<T> userData;
    private BooleanProperty selectedNow = new SimpleBooleanProperty(false);
    private final CRUDController<T> controller;

    public CRUDModel(CRUDController<T> controller) {
        this.controller = controller;
        userData = FXCollections.observableArrayList(getListBindings());
        //Если есть поле поиска
        if (controller.getSearch() != null) {
            FilteredList<T> filteredList = new FilteredList<>(userData);
            controller.getSearch().addListener((observable, oldValue, newValue) ->
                    filteredList.setPredicate(t -> {
                        if (newValue.length() < 3)
                            return true;
                        else {
                            return t.getName().toLowerCase().contains(newValue.toLowerCase());
                        }
                    }));

            controller.setUserData(filteredList);
        } else {
            controller.setUserData(userData);
        }

        controller.getSelection().selectedItemProperty().addListener(this::onSelectItem);
    }

    private T selected;

    private void onSelectItem(ObservableValue<?> observable, T oldValue, T newValue) {
        if (oldValue != null)
            controller.removeActive(oldValue);
        controller.setActive(newValue);
        selectedNow.set(newValue != null);
        selected = newValue;
    }


    public void deleteSelected() {
        if (selected != null)
            userData.remove(selected);
    }


    public void restoreServer() {
        userData.clear();
        serverData.sort(Comparator.comparingInt(ServerData::getId).reversed());
        //noinspection unchecked
        serverData
                .stream()
                .map(t -> (T) t.createCopy())
                .forEach(c -> userData.add(c));
    }

    public abstract Callback<T, Observable[]> getListBindings();

    public void save() {
        Map<String, Long> duplicates = userData.stream().collect(groupingBy(T::getName, counting()));
        boolean hasDuplicate = false;
        StringBuilder errorBuilder = new StringBuilder();
        for (Map.Entry<String, Long> entry : duplicates.entrySet()) {
            Long count = entry.getValue();
            if (count > 1) {
                hasDuplicate = true;
                errorBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        if (hasDuplicate) {
            Utils.showWaitAlert(
                    Alert.AlertType.ERROR,
                    "Невозможно сохранить результат",
                    "У предметов есть дублируещиеся имена, пожалуйста переименуйте или удалите их",
                    errorBuilder.toString()
            );
            return;
        }
        //Содержит в себе элементы которые изменились или добавились
        Set<T> userCopy = new HashSet<>(userData);
        //То что добавить на сервер
        Set<T> addSet = new HashSet<>();
        //Оставляем только данные которые надо либо удалить либо изменить
        userCopy.removeIf(componentType -> {
            if (componentType.getId() == -1) {
                addSet.add(componentType);
                return true;
            } else
                return false;
        });
        //Создаём сет который потом будет использовать для удаления
        Set<T> deleteSet = new HashSet<>(serverData);
        //Создаём сет который содержить изменённые объекты
        Set<T> editedSet = new HashSet<>();
        for (T userVersion : userCopy) {
            T serverVersion = serverData
                    .stream()
                    .filter(d -> d.getId() == userVersion.getId())
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Impossible"));
            //Пользователь не удалял данный пункт
            deleteSet.remove(serverVersion);
            //Если пользователь не трогал данный объект
            if (userVersion.isSimilar(serverVersion))
                continue;
            //Данный объект был изменён
            editedSet.add(userVersion);
        }
        int allSize = editedSet.size() + deleteSet.size() + addSet.size();
        if (allSize == 0) {
            Utils.showWaitAlert(
                    Alert.AlertType.WARNING,
                    "Изменений не найдено",
                    "Сохранение без изменений",
                    "Для того чтобы сохранить что нибудь ненужное, нужно сначала изменить что нибудь не нужное, а у нас изменений нет"
            );
        } else {
            saveAll(allSize, addSet, editedSet, deleteSet);
        }
    }

    private void saveAll(int allSize, Set<T> addSet, Set<T> editSet, Set<T> deleteSet) {
        InformativeLoad load = InformativeLoad.createLoad();
        getController().setInQuery(true);
        RequestManager.getInstance().runTaskAsync(() -> {
            boolean allOk = true;
            AtomicInteger progress = new AtomicInteger(0);
            for (T item : addSet) {
                Platform.runLater(() -> load.update(allSize, progress.incrementAndGet(), "Добавление на сервер " + item.getName()));
                try {
                    item.add();
                } catch (Exception ex) {
                    Platform.runLater(() -> Utils.showServerError(ex));
                    allOk = false;
                    continue;
                }
                getServerData().add(item);
            }
            for (T item : editSet) {
                Platform.runLater(() -> load.update(allSize, progress.incrementAndGet(), "Обновление на сервере " + item.getName()));
                try {
                    item.update();
                } catch (IOException | MethodExecuteException e) {
                    Platform.runLater(() -> Utils.showServerError(e));
                    allOk = false;
                    continue;
                }
                //Поскольку в editSet копии, то сначала придётся удалить а потом добавить новый
                getServerData().removeIf(d -> d.getId() == item.getId());
                getServerData().add(item);

            }
            StringBuilder constraint = new StringBuilder();
            for (T item : deleteSet) {
                Platform.runLater(() -> load.update(allSize, progress.incrementAndGet(), "Удаление " + item.getName()));
                DataDeleteResponse response = null;
                try {
                    response = item.delete();
                } catch (IOException | MethodExecuteException e) {
                    Platform.runLater(() -> Utils.showServerError(e));
                    allOk = false;
                    continue;
                }
                switch (response.getResponse()) {
                    case OK:
                        getServerData().removeIf(componentType -> componentType.getId() == item.getId());
                        break;
                    case ERROR:
                        allOk = false;
                        constraint.append("Не удалось удалить ").append(item.getName()).append(", он привязан к другим сущностям\n");
                        break;
                }
            }
            boolean finalAllOk = allOk;
            Platform.runLater(() -> {
                load.destroy();
                getController().setInQuery(false);
                Utils.showWaitAlert(
                        finalAllOk ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING,
                        "Операция завершена",
                        finalAllOk ? "Отправка данных на сервер успешно завершена" : "В ходе выполнения возникли ошибки",
                        constraint.toString()
                );
                restoreServer();
            });
        });
    }

    public void createNew(T type) {
        userData.add(0, type);
        controller.getSelection().select(0);
    }
}
