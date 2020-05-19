package ru.spliterash.pcmasterclient;

import com.google.gson.Gson;
import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import net.harawata.appdirs.AppDirsFactory;
import org.hildan.fxgson.FxGsonBuilder;
import ru.spliterash.pcmasterclient.api.RequestManager;
import ru.spliterash.pcmasterclient.api.methods.self.SelfExecutor;
import ru.spliterash.pcmasterclient.api.models.PcMasterUser;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

@Getter
public class Main extends Application {
    public static final File mainFolder = new File(AppDirsFactory.getInstance().getUserDataDir("PcMaster", "1.0", "Spliterash"));
    private AppSettings settings;
    private File configFile;
    private final Gson gson;

    private Stage mainStage;
    private Scene mainScene;
    @Getter
    private static Main main;
    private PcMasterUser user;

    public Main() {
        gson = new FxGsonBuilder().acceptNullProperties().create();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Открывает приложение
     */
    public void update() {
        if (settings.getToken() == null) {
            openLogin();
        } else
            RequestManager.getExecutor(SelfExecutor.class).executeSync(new Object(), (pcMasterUser, throwable) -> {
                if (pcMasterUser != null) {
                    this.user = pcMasterUser;
                    openApp();
                } else {
                    openLogin();
                }
            });
    }

    @SneakyThrows
    private void openApp() {
        closeCommonsWindows();
        Parent show;
        if (user == null) {
            throw new RuntimeException("User is null");
        }
        switch (user.getStatus()) {
            case USER:
                show = FXMLLoader.load(getClass().getResource("view/user/UserRoot.fxml"));
                break;
            case ADMIN:
                show = FXMLLoader.load(getClass().getResource("view/admin/AdminRoot.fxml"));
                break;
            default:
                throw new IllegalArgumentException("No role");
        }
//        Parent show = FXMLLoader.load(getClass().getResource("view/admin/AdminRoot.fxml"));

        mainScene = new Scene(show);
        mainStage.setTitle("PcMaster");
        mainStage.setScene(mainScene);
        mainStage.show();
        mainStage.setMinWidth(350);
        mainStage.setMinHeight(350);

    }

    @SneakyThrows
    public void openLogin() {
        closeCommonsWindows();
        Parent show;
        show = FXMLLoader.load(getClass().getResource("view/login/Login.fxml"));

        mainScene = new Scene(show);
        mainStage.setTitle("Авторизация");
        mainStage.setScene(mainScene);
        mainStage.show();
    }

    private void closeCommonsWindows() {
        ObservableList<Stage> stages = StageHelper.getStages();
        stages.removeIf(stage -> {
            if (stage.equals(mainStage))
                return false;
            stage.close();
            return true;
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main = this;
        mainStage = primaryStage;
        loadSettings();
        loadFonts();
        update();
    }

    public File getResourceFile(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null)
            return null;
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file;
    }

    private void loadFonts() throws IOException {
        File f = getResourceFile("fonts");
        for (File file : f.listFiles()) {
            FileInputStream stream = new FileInputStream(file);
            Font.loadFont(stream, 12);
        }
    }


    @Override
    public void stop() {
        saveSettings();
        RequestManager.getInstance().shutdownPool();
    }

    private void loadSettings() {
        mainFolder.mkdirs();
        configFile = new File(mainFolder, "config.json");
        if (configFile.isFile()) {
            try {
                settings = gson.fromJson(new InputStreamReader(new FileInputStream(configFile)), AppSettings.class);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("File read error");
                alert.setHeaderText("Failed to read settings file");
                alert.setContentText(ex.getLocalizedMessage());
            }
        }
        if (settings == null)
            settings = new AppSettings();

    }

    public void saveSettings() {
        try (OutputStreamWriter writer = new FileWriter(configFile)) {
            getGson().toJson(settings, writer);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("File save error");
            alert.setHeaderText("Failed to save settings file");
            alert.setContentText(ex.getLocalizedMessage());
        }
    }

    public void logOut() {
        settings.setToken(null);
        RequestManager.getInstance().resetAll();
        saveSettings();
        openLogin();
        //Очищаем окна
        System.gc();
    }
}