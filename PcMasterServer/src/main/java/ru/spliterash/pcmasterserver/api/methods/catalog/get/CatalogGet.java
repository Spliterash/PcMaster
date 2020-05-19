package ru.spliterash.pcmasterserver.api.methods.catalog.get;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.api.models.Component;
import ru.spliterash.pcmasterserver.api.models.ComponentType;
import ru.spliterash.pcmasterserver.api.models.Supplier;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;
import ru.spliterash.pcmasterserver.secutiry.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class CatalogGet extends AbstractExecutor<CatalogGetRequest, CatalogGetResponse> {

    @Override
    public CatalogGetResponse execute(CatalogGetRequest json, PcMasterUser user) {
        HttpServletRequest request = Utils.getRequest();
        //ИНФА О КАТЕГОРИЯХ
        JdbcTemplate template = getQueries().getTemplate().getJdbcTemplate();
        SqlRowSet componentTypes = template.queryForRowSet("" +
                "SELECT id,name, image_url, description,required FROM component_type order by id DESC"
        );
        boolean replace = json.isReplacePlaceholders();
        CatalogGetResponse.CatalogGetResponseBuilder response = CatalogGetResponse.builder();
        while (componentTypes.next()) {
            ComponentType type = new ComponentType(
                    componentTypes.getInt("id"),
                    Utils.replacePlaceholders(componentTypes.getString("name"),replace),
                    Utils.replacePlaceholders(componentTypes.getString("image_url"),replace),
                    Utils.replacePlaceholders(componentTypes.getString("description"),replace),
                    1 == componentTypes.getInt("required")
            );
            response.componentType(type);
        }
        //ИНФА О ПОСТАВЩИКАХ
        SqlRowSet supplierSet = template.queryForRowSet("SELECT id,name, image_url FROM supplier order by id DESC");
        while (supplierSet.next()) {
            Supplier supplier = new Supplier(
                    supplierSet.getInt("id"),
                    Utils.replacePlaceholders(supplierSet.getString("name"),replace),
                    Utils.replacePlaceholders(supplierSet.getString("image_url"),replace)
            );
            response.supplier(supplier);
        }
        //ИНФА О САМИХ КОМПОНЕНТАХ
        //ЗАЧЕМ Я КАПСОМ ПИШУ Я НЕ ЗНАЮ
        SqlRowSet set = template.queryForRowSet("SELECT \n" +
                "    c.id,\n" +
                "    c.name,\n" +
                "    c.price,\n" +
                "    c.image_url,\n" +
                "    c.component_type_id,\n" +
                "    c.supplier_id \n" +
                "FROM\n" +
                "    `component` c\n" +
                "   ORDER BY c.id DESC");
        List<Component> components = new ArrayList<>();
        while (set.next()) {
            Component component = new Component(
                    set.getInt("id"),
                    Utils.replacePlaceholders(set.getString("name"),replace),
                    set.getInt("supplier_id"),
                    set.getInt("price"),
                    Utils.replacePlaceholders(set.getString("image_url"),replace),
                    set.getInt("component_type_id")
            );
            components.add(component);
        }

        response.components(components);
        return response.build();
    }
}
