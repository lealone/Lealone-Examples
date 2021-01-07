package org.lealone.opscenter.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'admin_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface AdminService {

    static AdminService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.opscenter.service.AdminServiceImpl();
        else
            return new ServiceProxy(url);
    }

    String login(String username, String password);

    String save();

    static class ServiceProxy implements AdminService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE LOGIN(?, ?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE SAVE()");
        }

        @Override
        public String login(String username, String password) {
            try {
                ps1.setString(1, username);
                ps1.setString(2, password);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.LOGIN", e);
            }
        }

        @Override
        public String save() {
            try {
                ResultSet rs = ps2.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.SAVE", e);
            }
        }
    }
}
