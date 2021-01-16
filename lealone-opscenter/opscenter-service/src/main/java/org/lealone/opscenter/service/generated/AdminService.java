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

    String login(String password);

    String logout();

    String save(String port, String allowOthers, String ssl);

    String admin();

    String startTranslate();

    String shutdown();

    String tools(String tool, String args);

    static class ServiceProxy implements AdminService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;
        private final PreparedStatement ps3;
        private final PreparedStatement ps4;
        private final PreparedStatement ps5;
        private final PreparedStatement ps6;
        private final PreparedStatement ps7;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE LOGIN(?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE LOGOUT()");
            ps3 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE SAVE(?, ?, ?)");
            ps4 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE ADMIN()");
            ps5 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE START_TRANSLATE()");
            ps6 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE SHUTDOWN()");
            ps7 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE ADMIN_SERVICE TOOLS(?, ?)");
        }

        @Override
        public String login(String password) {
            try {
                ps1.setString(1, password);
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
        public String logout() {
            try {
                ResultSet rs = ps2.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.LOGOUT", e);
            }
        }

        @Override
        public String save(String port, String allowOthers, String ssl) {
            try {
                ps3.setString(1, port);
                ps3.setString(2, allowOthers);
                ps3.setString(3, ssl);
                ResultSet rs = ps3.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.SAVE", e);
            }
        }

        @Override
        public String admin() {
            try {
                ResultSet rs = ps4.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.ADMIN", e);
            }
        }

        @Override
        public String startTranslate() {
            try {
                ResultSet rs = ps5.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.START_TRANSLATE", e);
            }
        }

        @Override
        public String shutdown() {
            try {
                ResultSet rs = ps6.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.SHUTDOWN", e);
            }
        }

        @Override
        public String tools(String tool, String args) {
            try {
                ps7.setString(1, tool);
                ps7.setString(2, args);
                ResultSet rs = ps7.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("ADMIN_SERVICE.TOOLS", e);
            }
        }
    }
}
