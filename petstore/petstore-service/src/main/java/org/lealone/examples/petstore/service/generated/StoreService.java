package org.lealone.examples.petstore.service.generated;

import java.sql.*;
import java.sql.Blob;
import org.lealone.client.ClientServiceProxy;
import org.lealone.examples.petstore.dal.model.Category;
import org.lealone.examples.petstore.dal.model.Item;
import org.lealone.examples.petstore.dal.model.Product;
import org.lealone.orm.json.JsonObject;

/**
 * Service interface for 'store_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface StoreService {

    static StoreService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.examples.petstore.service.StoreServiceImpl();
        else
            return new ServiceProxy(url);
    }

    Category getCategory(String categoryId);

    String addProduct(Product product, String categoryId, Blob picture);

    String getAllCategories();

    String getAllProductItems(String productId);

    Item getProductItem(String itemId);

    String getCartItems(String cart);

    static class ServiceProxy implements StoreService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;
        private final PreparedStatement ps3;
        private final PreparedStatement ps4;
        private final PreparedStatement ps5;
        private final PreparedStatement ps6;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_CATEGORY(?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE ADD_PRODUCT(?, ?, ?)");
            ps3 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_ALL_CATEGORIES()");
            ps4 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_ALL_PRODUCT_ITEMS(?)");
            ps5 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_PRODUCT_ITEM(?)");
            ps6 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_CART_ITEMS(?)");
        }

        @Override
        public Category getCategory(String categoryId) {
            try {
                ps1.setString(1, categoryId);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                JsonObject jo = new JsonObject(rs.getString(1));
                rs.close();
                return jo.mapTo(Category.class);
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.GET_CATEGORY", e);
            }
        }

        @Override
        public String addProduct(Product product, String categoryId, Blob picture) {
            try {
                ps2.setString(1, JsonObject.mapFrom(product).encode());
                ps2.setString(2, categoryId);
                ps2.setBlob(3, picture);
                ResultSet rs = ps2.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.ADD_PRODUCT", e);
            }
        }

        @Override
        public String getAllCategories() {
            try {
                ResultSet rs = ps3.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.GET_ALL_CATEGORIES", e);
            }
        }

        @Override
        public String getAllProductItems(String productId) {
            try {
                ps4.setString(1, productId);
                ResultSet rs = ps4.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.GET_ALL_PRODUCT_ITEMS", e);
            }
        }

        @Override
        public Item getProductItem(String itemId) {
            try {
                ps5.setString(1, itemId);
                ResultSet rs = ps5.executeQuery();
                rs.next();
                JsonObject jo = new JsonObject(rs.getString(1));
                rs.close();
                return jo.mapTo(Item.class);
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.GET_PRODUCT_ITEM", e);
            }
        }

        @Override
        public String getCartItems(String cart) {
            try {
                ps6.setString(1, cart);
                ResultSet rs = ps6.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.GET_CART_ITEMS", e);
            }
        }
    }
}
