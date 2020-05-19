package ru.spliterash.pcmasterclient.view.admin;

import ru.spliterash.pcmasterclient.view.admin.pages.component.ComponentPage;
import ru.spliterash.pcmasterclient.view.admin.pages.componentType.ComponentTypePage;
import ru.spliterash.pcmasterclient.view.admin.pages.orders.OrderPage;
import ru.spliterash.pcmasterclient.view.admin.pages.reports.Report;
import ru.spliterash.pcmasterclient.view.admin.pages.supplier.SupplierPage;
import ru.spliterash.pcmasterclient.view.other.AbstractAppController;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminRootController extends AbstractAppController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButton("componentType", new ComponentTypePage());
        addButton("supplier", new SupplierPage());
        addButton("component", new ComponentPage());
        addButton("order", new OrderPage());
        addButton("report", new Report());
    }
}
