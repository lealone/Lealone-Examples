package org.lealone.examples.petstore.service.generated.executor;

import java.sql.Blob;
import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.petstore.dal.model.Category;
import org.lealone.examples.petstore.dal.model.Item;
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
        case "GET_CATEGORY":
            String p_categoryId_1 = methodArgs[0].getString();
            Category result1 = this.s.getCategory(p_categoryId_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(JsonObject.mapFrom(result1).encode());
        case "ADD_PRODUCT":
            Product p_product_2 =  new JsonObject(methodArgs[0].getString()).mapTo(Product.class);
            String p_categoryId_2 = methodArgs[1].getString();
            Blob p_picture_2 = methodArgs[2].getBlob();
            String result2 = this.s.addProduct(p_product_2, p_categoryId_2, p_picture_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        case "GET_ALL_CATEGORIES":
            String result3 = this.s.getAllCategories();
            if (result3 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result3);
        case "GET_ALL_PRODUCT_ITEMS":
            String p_productId_4 = methodArgs[0].getString();
            String result4 = this.s.getAllProductItems(p_productId_4);
            if (result4 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result4);
        case "GET_PRODUCT_ITEM":
            String p_itemId_5 = methodArgs[0].getString();
            Item result5 = this.s.getProductItem(p_itemId_5);
            if (result5 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(JsonObject.mapFrom(result5).encode());
        case "GET_CART_ITEMS":
            String p_cart_6 = methodArgs[0].getString();
            String result6 = this.s.getCartItems(p_cart_6);
            if (result6 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result6);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "GET_CATEGORY":
            String p_categoryId_1 = ServiceExecutor.toString("CATEGORY_ID", methodArgs);
            Category result1 = this.s.getCategory(p_categoryId_1);
            if (result1 == null)
                return null;
            return JsonObject.mapFrom(result1).encode();
        case "ADD_PRODUCT":
            Product p_product_2 =  new JsonObject(ServiceExecutor.toString("PRODUCT", methodArgs)).mapTo(Product.class);
            String p_categoryId_2 = ServiceExecutor.toString("CATEGORY_ID", methodArgs);
            Blob p_picture_2 = new org.lealone.db.value.ReadonlyBlob(ServiceExecutor.toString("PICTURE", methodArgs));
            String result2 = this.s.addProduct(p_product_2, p_categoryId_2, p_picture_2);
            if (result2 == null)
                return null;
            return result2;
        case "GET_ALL_CATEGORIES":
            String result3 = this.s.getAllCategories();
            if (result3 == null)
                return null;
            return result3;
        case "GET_ALL_PRODUCT_ITEMS":
            String p_productId_4 = ServiceExecutor.toString("PRODUCT_ID", methodArgs);
            String result4 = this.s.getAllProductItems(p_productId_4);
            if (result4 == null)
                return null;
            return result4;
        case "GET_PRODUCT_ITEM":
            String p_itemId_5 = ServiceExecutor.toString("ITEM_ID", methodArgs);
            Item result5 = this.s.getProductItem(p_itemId_5);
            if (result5 == null)
                return null;
            return JsonObject.mapFrom(result5).encode();
        case "GET_CART_ITEMS":
            String p_cart_6 = ServiceExecutor.toString("CART", methodArgs);
            String result6 = this.s.getCartItems(p_cart_6);
            if (result6 == null)
                return null;
            return result6;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "GET_CATEGORY":
            ja = new JsonArray(json);
            String p_categoryId_1 = ja.getString(0);
            Category result1 = this.s.getCategory(p_categoryId_1);
            if (result1 == null)
                return null;
            return JsonObject.mapFrom(result1).encode();
        case "ADD_PRODUCT":
            ja = new JsonArray(json);
            Product p_product_2 = ja.getJsonObject(0).mapTo(Product.class);
            String p_categoryId_2 = ja.getString(1);
            Blob p_picture_2 = ja.getJsonObject(2).mapTo(java.sql.Blob.class);
            String result2 = this.s.addProduct(p_product_2, p_categoryId_2, p_picture_2);
            if (result2 == null)
                return null;
            return result2;
        case "GET_ALL_CATEGORIES":
            String result3 = this.s.getAllCategories();
            if (result3 == null)
                return null;
            return result3;
        case "GET_ALL_PRODUCT_ITEMS":
            ja = new JsonArray(json);
            String p_productId_4 = ja.getString(0);
            String result4 = this.s.getAllProductItems(p_productId_4);
            if (result4 == null)
                return null;
            return result4;
        case "GET_PRODUCT_ITEM":
            ja = new JsonArray(json);
            String p_itemId_5 = ja.getString(0);
            Item result5 = this.s.getProductItem(p_itemId_5);
            if (result5 == null)
                return null;
            return JsonObject.mapFrom(result5).encode();
        case "GET_CART_ITEMS":
            ja = new JsonArray(json);
            String p_cart_6 = ja.getString(0);
            String result6 = this.s.getCartItems(p_cart_6);
            if (result6 == null)
                return null;
            return result6;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
