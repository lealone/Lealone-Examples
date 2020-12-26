package org.lealone.examples.petstore.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;
import org.lealone.examples.petstore.dal.model.User;
import org.lealone.orm.json.JsonObject;

/**
 * Service interface for 'user_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface UserService {

    static UserService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.examples.petstore.service.UserServiceImpl();
        else
            return new ServiceProxy(url);
    }

    User login(String userId, String password);

    void register(User user);

    void update(User user);

    User getUser(String userId);

    static class ServiceProxy implements UserService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;
        private final PreparedStatement ps3;
        private final PreparedStatement ps4;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE USER_SERVICE LOGIN(?, ?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE USER_SERVICE REGISTER(?)");
            ps3 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE USER_SERVICE UPDATE(?)");
            ps4 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE USER_SERVICE GET_USER(?)");
        }

        @Override
        public User login(String userId, String password) {
            try {
                ps1.setString(1, userId);
                ps1.setString(2, password);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                JsonObject jo = new JsonObject(rs.getString(1));
                rs.close();
                return jo.mapTo(User.class);
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("USER_SERVICE.LOGIN", e);
            }
        }

        @Override
        public void register(User user) {
            try {
                ps2.setString(1, JsonObject.mapFrom(user).encode());
                ps2.executeUpdate();
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("USER_SERVICE.REGISTER", e);
            }
        }

        @Override
        public void update(User user) {
            try {
                ps3.setString(1, JsonObject.mapFrom(user).encode());
                ps3.executeUpdate();
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("USER_SERVICE.UPDATE", e);
            }
        }

        @Override
        public User getUser(String userId) {
            try {
                ps4.setString(1, userId);
                ResultSet rs = ps4.executeQuery();
                rs.next();
                JsonObject jo = new JsonObject(rs.getString(1));
                rs.close();
                return jo.mapTo(User.class);
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("USER_SERVICE.GET_USER", e);
            }
        }
    }
}
