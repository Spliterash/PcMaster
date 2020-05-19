package ru.spliterash.pcmasterclient.view.login;

import javafx.beans.value.ObservableValue;
import lombok.Getter;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Getter
public class PasswordCheckerModel extends LabelTextModel {
    private Predicate<String> hasDigit = Pattern.compile("[0-9]").asPredicate();
    private Predicate<String> hasSmall = Pattern.compile("[a-z]").asPredicate();
    private Predicate<String> hasBig = Pattern.compile("[A-Z]").asPredicate();
    private static final CharsetEncoder encoder = StandardCharsets.US_ASCII.newEncoder();

    @Override
    public void onChanged(ObservableValue<? extends String> observable, String oldValue, String pass) {
        long v = pass.length();
        boolean ok = true;
        clearList();
        if (v < 8) {
            addLine("Пароль должен быть как минимум 8 символов");
            ok = false;
        }
        if (!hasSmall.test(pass)) {
            addLine("Пароль должен содержать как минимум 1 маленькую букву");
            ok = false;
        }
        if (!hasBig.test(pass)) {
            addLine("Пароль должен содержать как минимум 1 большую букву");
            ok = false;
        }
        if (!hasDigit.test(pass)) {
            addLine("Пароль должен содержать как минимум 1 цифру");
            ok = false;
        }
        if (!encoder.canEncode(pass)) {
            addLine("Разрешается использовать только символы англиского алфавита");
            ok = false;
        }
        if (ok) {
            ok();
        }
        isValidProperty.set(ok);
    }

}
