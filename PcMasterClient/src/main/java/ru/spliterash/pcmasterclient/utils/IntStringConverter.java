package ru.spliterash.pcmasterclient.utils;

import javafx.util.StringConverter;

public class IntStringConverter extends StringConverter<Integer> {
    @Override
    public String toString(Integer object) {
        return object.toString();
    }

    @Override
    public Integer fromString(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ex) {
            return -1;
        }
    }
}
