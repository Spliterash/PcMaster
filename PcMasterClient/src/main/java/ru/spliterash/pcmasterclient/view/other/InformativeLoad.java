package ru.spliterash.pcmasterclient.view.other;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import ru.spliterash.pcmasterclient.Utils;

import java.io.IOException;

public class InformativeLoad extends AnchorPane {

    @FXML
    private ProgressBar bar;

    @FXML
    private ProgressIndicator indicator;

    @FXML
    private Label info;

    public static InformativeLoad createLoad() {
        InformativeLoad bar = new InformativeLoad();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(bar));
        stage.show();
        return bar;
    }

    public void setProgress(double progress) {
        bar.setProgress(progress);
        indicator.setProgress(progress);
        if (progress >= 1) {
            destroy();
        }
    }

    public void setInfo(String text) {
        info.setText(text);
    }

    private InformativeLoad() {
        Utils.loadFXML(this);
    }

    public void update(int all, int current, String text) {
        double progress = (double) current / (double) all;
        setProgress(progress);
        setInfo(text);
    }

    public void destroy() {
        Stage stage = (Stage) getScene().getWindow();
        stage.close();
    }
}

