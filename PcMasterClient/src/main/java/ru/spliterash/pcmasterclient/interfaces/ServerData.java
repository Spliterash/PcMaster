package ru.spliterash.pcmasterclient.interfaces;

import ru.spliterash.pcmasterclient.api.MethodExecuteException;
import ru.spliterash.pcmasterclient.api.methods.catalog.add.DataAddResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.delete.DataDeleteResponse;
import ru.spliterash.pcmasterclient.api.methods.catalog.update.DataUpdateResponse;

import java.io.IOException;

public interface ServerData {
    int getId();

    boolean isSimilar(ServerData another);

    String getName();

    ServerData createCopy();

    //Серверные методы
    //Выполняется в том же потоке откуда вызвали
    //Так что осторожнее

    /**
     * Изменяет ID предмета
     * если ок
     * Если же с таким названием есть тоже изменяет на него
     */
    DataAddResponse add() throws IOException, MethodExecuteException;

    DataUpdateResponse update() throws IOException, MethodExecuteException;

    DataDeleteResponse delete() throws IOException, MethodExecuteException;
}
