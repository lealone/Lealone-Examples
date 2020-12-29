package org.lealone.examples.petstore.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;
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

    String addProduct(Product product, String logo);

    String getAllCategories();

    String getAllProductItems(String productId);

    static class ServiceProxy implements StoreService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;
        private final PreparedStatement ps3;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE ADD_PRODUCT(?, ?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_ALL_CATEGORIES()");
            ps3 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE STORE_SERVICE GET_ALL_PRODUCT_ITEMS(?)");
        }

        @Override
        public String addProduct(Product product, String logo) {
            try {
                ps1.setString(1, JsonObject.mapFrom(product).encode());
                ps1.setString(2, logo);
                ResultSet rs = ps1.executeQuery();
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
                ResultSet rs = ps2.executeQuery();
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
                ps3.setString(1, productId);
                ResultSet rs = ps3.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("STORE_SERVICE.GET_ALL_PRODUCT_ITEMS", e);
            }
        }
    }
}
