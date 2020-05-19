package ru.spliterash.pcmasterclient.view.login;

import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import ru.spliterash.pcmasterclient.Main;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.auth.login.AuthLogin;
import ru.spliterash.pcmasterclient.api.methods.auth.login.AuthLoginRequest;
import ru.spliterash.pcmasterclient.api.methods.auth.register.AuthRegister;
import ru.spliterash.pcmasterclient.api.methods.auth.register.AuthRegisterRequest;

import java.net.URL;
import java.util.*;

public class LoginController implements Initializable {
    @FXML
    private TextFlow lLoginHelp;
    @FXML
    private TextFlow lPasswordHelp;
    @FXML
    private Button loginButton;
    @FXML
    private Button regButton;
    @FXML
    private TextField lLogin;
    @FXML
    private PasswordField lPassword;
    @FXML
    private TextField rLogin;
    @FXML
    private PasswordField rPassword;
    @FXML
    private TextField rLastName;
    @FXML
    private TextField rFirstName;
    @FXML
    private TextField rMiddleName;
    @FXML
    private TextFlow rLoginHelp;
    @FXML
    private TextFlow rPasswordHelp;
    @FXML
    private TextFlow rFirstNameHelp;
    @FXML
    private TextFlow rLastNameHelp;
    @FXML
    private TextFlow rMiddleNameHelp;

    /**
     * Пользователь хочет войти
     */
    @FXML
    void onLogin(ActionEvent event) {
        RequestManager.getInstance().getExecutor(AuthLogin.class).executeSync(new AuthLoginRequest(lLogin.getText(), lPassword.getText()), (authLoginResponse, throwable) -> {
            if (throwable != null) {
                Utils.showServerError(throwable);
            } else {
                String token = authLoginResponse.getToken();
                if (token == null) {
                    Utils.showWaitAlert(
                            Alert.AlertType.ERROR,
                            "Неверные данные",
                            "Логин или пароль введены неверно",
                            "Повторите попытку");
                } else {
                    Main.getMain().getSettings().setToken(token);
                    Main.getMain().saveSettings();
                    Main.getMain().update();
                }
            }
        }, loginButton.getParent());
    }

    /**
     * Пользователь хочет зарегистрироваться
     */
    @FXML
    private void onRegister(ActionEvent event) {
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .firstName(rFirstName.getText())
                .lastName(rLastName.getText())
                .middleName(rMiddleName.getText())
                .password(rPassword.getText())
                .username(rLogin.getText())
                .build();
        RequestManager.getInstance().getExecutor(AuthRegister.class).executeSync(request, (authRegisterResponse, throwable) -> {
            if (throwable != null) {
                Utils.showServerError(throwable);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                switch (authRegisterResponse.getResponse()) {
                    case OK:
                        alert.setTitle("Всё ок");
                        alert.setContentText("Теперь вы можете войти");
                        alert.setOnCloseRequest(c -> {
                            copyToLogin();
                        });
                        break;
                    case USERNAME_TAKEN:
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Логин " + rLogin.getText() + " уже занят");
                        alert.setContentText("Пожалуйста выберите другой логин");
                        break;
                }
                alert.initOwner(Main.getMain().getMainStage());
                alert.showAndWait();
            }
        }, loginButton.getParent());
    }

    private Map<TextField, Boolean> loginMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Настройки для блока входа
        {
            Set<BooleanProperty> valid = new HashSet<>();
            //Логин вход
            {
                LoginCheckerModel checker = new LoginCheckerModel();
                checker.autoBind(lLoginHelp, lLogin);
                valid.add(checker.getIsValidProperty());
            }
            //Пароль вход
            {
                PasswordCheckerModel checker = new PasswordCheckerModel();
                checker.autoBind(lPasswordHelp, lPassword);
                valid.add(checker.getIsValidProperty());
            }
            //Назначаем кнопку входа
            {
                loginButton.disableProperty().bind(Utils.andProperty(valid).not());
            }
        }
        //Настройки для блока регистрации
        {
            Set<BooleanProperty> valid = new HashSet<>();
            //Логин регистрация
            {
                LoginCheckerModel checker = new LoginCheckerModel();
                checker.autoBind(rLoginHelp, rLogin);
                valid.add(checker.getIsValidProperty());
            }
            //Пароль регистрация
            {
                PasswordCheckerModel checker = new PasswordCheckerModel();
                checker.autoBind(rPasswordHelp, rPassword);
                valid.add(checker.getIsValidProperty());
            }
            //ФИО
            {
                //Фамилия
                {
                    EmptyCheckerModel checker = new EmptyCheckerModel();
                    checker.autoBind(rLastNameHelp, rLastName);
                    valid.add(checker.getIsValidProperty());
                } //Имя
                {
                    EmptyCheckerModel checker = new EmptyCheckerModel();
                    checker.autoBind(rFirstNameHelp, rFirstName);
                    valid.add(checker.getIsValidProperty());
                } //Отчество
                {
                    EmptyCheckerModel checker = new EmptyCheckerModel();
                    checker.autoBind(rMiddleNameHelp, rMiddleName);
                    valid.add(checker.getIsValidProperty());
                }
            }
            //Назначаем кнопку регистрации
            {
                regButton.disableProperty().bind(Utils.andProperty(valid).not());
            }
        }
    }


    public void copyToLogin() {
        lLogin.setText(rLogin.getText());
        lPassword.setText(rPassword.getText());
    }
}
