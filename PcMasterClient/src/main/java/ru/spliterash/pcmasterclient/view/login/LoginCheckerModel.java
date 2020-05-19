package ru.spliterash.pcmasterclient.view.login;

import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LoginCheckerModel extends LabelTextModel {
    private static final Predicate<String> lettersCheck = Pattern.compile("[^A-Za-z0-9]+").asPredicate();

    @Override
    public void onChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        long v = newValue.length();
        boolean ok = true;
        clearList();
        if (v < 4 || v > 16) {
            addLine("Имя пользователя может быть в пределах от 4 до 16 символов");
            ok = false;
        }
        if (lettersCheck.test(newValue)) {
            addLine("Имя пользователя может содержать только латинские буквы и цифры,а так же знак подчёркивания (_)");
            ok = false;
        }
        if (ok) {
            addLine("Всё верно", Color.valueOf("#7bff15"));
        }
        isValidProperty.set(ok);
    }
}
