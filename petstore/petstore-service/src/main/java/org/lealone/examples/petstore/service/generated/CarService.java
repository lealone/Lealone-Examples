package org.lealone.examples.petstore.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'car_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface CarService {

    static CarService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.examples.petstore.service.CarServiceImpl();
        else
            return new ServiceProxy(url);
    }

    void addItem(String carId, String itemId);

    void removeItem(String carId, String itemId);

    void update(String carId, String itemId, Integer quantity);

    String getItems(String carId);

    static class ServiceProxy implements CarService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;
        private final PreparedStatement ps3;
        private final PreparedStatement ps4;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE CAR_SERVICE ADD_ITEM(?, ?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE CAR_SERVICE REMOVE_ITEM(?, ?)");
            ps3 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE CAR_SERVICE UPDATE(?, ?, ?)");
            ps4 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE CAR_SERVICE GET_ITEMS(?)");
        }

        @Override
        public void addItem(String carId, String itemId) {
            try {
                ps1.setString(1, carId);
                ps1.setString(2, itemId);
                ps1.executeUpdate();
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("CAR_SERVICE.ADD_ITEM", e);
            }
        }

        @Override
        public void removeItem(String carId, String itemId) {
            try {
                ps2.setString(1, carId);
                ps2.setString(2, itemId);
                ps2.executeUpdate();
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("CAR_SERVICE.REMOVE_ITEM", e);
            }
        }

        @Override
        public void update(String carId, String itemId, Integer quantity) {
            try {
                ps3.setString(1, carId);
                ps3.setString(2, itemId);
                ps3.setInt(3, quantity);
                ps3.executeUpdate();
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("CAR_SERVICE.UPDATE", e);
            }
        }

        @Override
        public String getItems(String carId) {
            try {
                ps4.setString(1, carId);
                ResultSet rs = ps4.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("CAR_SERVICE.GET_ITEMS", e);
            }
        }
    }
}
