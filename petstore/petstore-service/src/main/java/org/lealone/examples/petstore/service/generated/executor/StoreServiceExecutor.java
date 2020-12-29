package org.lealone.examples.petstore.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.petstore.dal.model.Product;
import org.lealone.examples.petstore.service.StoreServiceImpl;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

/**
 * Service executor for 'store_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class StoreServiceExecutor implements ServiceExecutor {

    private final StoreServiceImpl s = new StoreServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "ADD_PRODUCT":
            Product p_product_1 =  new JsonObject(methodArgs[0].getString()).mapTo(Product.class);
            String p_logo_1 = methodArgs[1].getString();
            String result1 = this.s.addProduct(p_product_1, p_logo_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        case "GET_ALL_CATEGORIES":
            String result2 = this.s.getAllCategories();
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        case "GET_ALL_PRODUCT_ITEMS":
            String p_productId_3 = methodArgs[0].getString();
            String result3 = this.s.getAllProductItems(p_productId_3);
            if (result3 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result3);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "ADD_PRODUCT":
            Product p_product_1 =  new JsonObject(ServiceExecutor.toString("PRODUCT", methodArgs)).mapTo(Product.class);
            String p_logo_1 = ServiceExecutor.toString("LOGO", methodArgs);
            String result1 = this.s.addProduct(p_product_1, p_logo_1);
            if (result1 == null)
                return null;
            return result1;
        case "GET_ALL_CATEGORIES":
            String result2 = this.s.getAllCategories();
            if (result2 == null)
                return null;
            return result2;
        case "GET_ALL_PRODUCT_ITEMS":
            String p_productId_3 = ServiceExecutor.toString("PRODUCT_ID", methodArgs);
            String result3 = this.s.getAllProductItems(p_productId_3);
            if (result3 == null)
                return null;
            return result3;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "ADD_PRODUCT":
            ja = new JsonArray(json);
            Product p_product_1 = ja.getJsonObject(0).mapTo(Product.class);
            String p_logo_1 = ja.getString(1);
            String result1 = this.s.addProduct(p_product_1, p_logo_1);
            if (result1 == null)
                return null;
            return result1;
        case "GET_ALL_CATEGORIES":
            String result2 = this.s.getAllCategories();
            if (result2 == null)
                return null;
            return result2;
        case "GET_ALL_PRODUCT_ITEMS":
            ja = new JsonArray(json);
            String p_productId_3 = ja.getString(0);
            String result3 = this.s.getAllProductItems(p_productId_3);
            if (result3 == null)
                return null;
            return result3;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
