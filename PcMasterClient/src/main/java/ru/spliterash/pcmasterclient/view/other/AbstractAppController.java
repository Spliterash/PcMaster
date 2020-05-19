package ru.spliterash.pcmasterclient.view.other;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import ru.spliterash.pcmasterclient.Main;
import ru.spliterash.pcmasterclient.view.admin.Page;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAppController implements Initializable {
    @FXML
    private AnchorPane content;
    private Map<String, Page> buttonMap = new HashMap<>();
    private Button activeButton;
    protected void addButton(String str,Page page){
        buttonMap.put(str,page);
    }

    protected void setContent(Node node, Button button) {
        ObservableList<Node> children = content.getChildren();
        children.clear();
        children.add(node);
        if (activeButton != null)
            activeButton.getStyleClass().remove("toolbar_selected");
        activeButton = button;
        button.getStyleClass().add("toolbar_selected");
    }

    private Page activePage;

    @FXML
    private void onToolBarButton(ActionEvent event) {
        if (!(event.getSource() instanceof Button)) {
            return;
        }
        Button button = (Button) event.getSource();
        Object typeObj = button.getProperties().get("type");
        if (typeObj instanceof String) {
            Page page = buttonMap.get(typeObj.toString());
            if (page != null) {
                if (activePage != null)
                    activePage.onClose();
                activePage = page;
                page.onOpen();
                setContent(page.toNode(), button);
            }
        }
    }

    //Выход из приложения
    public void exit(ActionEvent event) {
        Main.getMain().logOut();
    }
}
