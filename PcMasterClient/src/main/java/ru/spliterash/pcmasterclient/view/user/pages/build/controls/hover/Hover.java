package ru.spliterash.pcmasterclient.view.user.pages.build.controls.hover;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import ru.spliterash.pcmasterclient.Utils;

import java.io.IOException;

public class Hover extends AnchorPane {
    @FXML
    private ImageView view;
    @FXML
    private Label action;

    public Hover(Image image, String action) {
        Utils.loadFXML(this);
        view.setImage(image);
        this.action.setText(action);
    }


}
