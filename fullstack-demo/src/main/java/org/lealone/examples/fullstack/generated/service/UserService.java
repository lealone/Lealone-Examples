package org.lealone.examples.fullstack.generated.service;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;
import org.lealone.examples.fullstack.generated.model.User;

/**
 * Service interface for 'user_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface UserService {

    Long addUser(String name, Integer age);

    User findByName(String name);

    static UserService create() {
        return create(null);
    }

    static UserService create(String url) {
        if (url == null)
            url = ClientServiceProxy.getUrl();

        if (ClientServiceProxy.isEmbedded(url))
            return new org.lealone.examples.fullstack.UserServiceImpl();
        else
            return new ServiceProxy(url);
    }

    static class ServiceProxy implements UserService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE USER_SERVICE ADD_USER(?, ?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE USER_SERVICE FIND_BY_NAME(?)");
        }

        @Override
        public Long addUser(String name, Integer age) {
            try {
                ps1.setString(1, name);
                ps1.setInt(2, age);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                Long ret = rs.getLong(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("USER_SERVICE.ADD_USER", e);
            }
        }

        @Override
        public User findByName(String name) {
            try {
                ps2.setString(1, name);
                ResultSet rs = ps2.executeQuery();
                rs.next();
                String ret = rs.getString(1);
                rs.close();
                return User.decode(ret);
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("USER_SERVICE.FIND_BY_NAME", e);
            }
        }
    }
}
