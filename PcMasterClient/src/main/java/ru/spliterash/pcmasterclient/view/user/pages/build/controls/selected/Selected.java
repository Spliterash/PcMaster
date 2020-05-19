package ru.spliterash.pcmasterclient.view.user.pages.build.controls.selected;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import ru.spliterash.pcmasterclient.Utils;
import ru.spliterash.pcmasterclient.api.models.Component;

import java.io.IOException;

public class Selected extends AnchorPane {


    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private ImageView componentPhoto;
    @FXML
    private ImageView supplierLogo;


    public Selected(Component component) {
        Utils.loadFXML(this);
        Utils.setImage(component.getImage(), componentPhoto);
        Utils.setImage(component.getSupplierFromCache().getImageUrl(), supplierLogo);
        name.setText(component.getName());
        price.setText("Цена: " + component.getPrice());
    }

}
