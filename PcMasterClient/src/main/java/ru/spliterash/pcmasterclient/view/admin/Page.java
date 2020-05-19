package ru.spliterash.pcmasterclient.view.admin;

import javafx.scene.Node;

public interface Page {
    default void onOpen() {
        //no impl
    }

    default void onClose() {
        //no impl
    }

    /**
     * Тупо кастует интерфейс в ноду
     * Да костыль но что поделать
     * В такое время живём
     */
    default Node toNode() {
        return (Node) this;
    }
}
