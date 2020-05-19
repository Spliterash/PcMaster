package ru.spliterash.pcmasterclient.view.user;

import ru.spliterash.pcmasterclient.view.controls.orders.UserOrder;
import ru.spliterash.pcmasterclient.view.other.AbstractAppController;
import ru.spliterash.pcmasterclient.view.user.pages.build.BuildPage;

import java.net.URL;
import java.util.ResourceBundle;

public class UserRootController extends AbstractAppController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButton("build", new BuildPage());
        addButton("orders", new UserOrder(true));
    }
}
