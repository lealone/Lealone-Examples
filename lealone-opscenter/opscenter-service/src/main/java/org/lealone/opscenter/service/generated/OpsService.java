package org.lealone.opscenter.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'ops_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface OpsService {

    static OpsService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.opscenter.service.OpsServiceImpl();
        else
            return new ServiceProxy(url);
    }

    String getLanguages();

    String getSettings(String setting);

    String settingSave(String name, String driver, String url, String user);

    String settingRemove(String name);

    String readTranslations(String language);

    String login(String url, String user, String password);

    String testConnection();

    static class ServiceProxy implements OpsService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;
        private final PreparedStatement ps3;
        private final PreparedStatement ps4;
        private final PreparedStatement ps5;
        private final PreparedStatement ps6;
        private final PreparedStatement ps7;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE GET_LANGUAGES()");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE GET_SETTINGS(?)");
            ps3 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE SETTING_SAVE(?, ?, ?, ?)");
            ps4 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE SETTING_REMOVE(?)");
            ps5 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE READ_TRANSLATIONS(?)");
            ps6 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE LOGIN(?, ?, ?)");
            ps7 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE OPS_SERVICE TEST_CONNECTION()");
        }

        @Override
        public String getLanguages() {
            try {
                ResultSet rs = ps1.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.GET_LANGUAGES", e);
            }
        }

        @Override
        public String getSettings(String setting) {
            try {
                ps2.setString(1, setting);
                ResultSet rs = ps2.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.GET_SETTINGS", e);
            }
        }

        @Override
        public String settingSave(String name, String driver, String url, String user) {
            try {
                ps3.setString(1, name);
                ps3.setString(2, driver);
                ps3.setString(3, url);
                ps3.setString(4, user);
                ResultSet rs = ps3.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.SETTING_SAVE", e);
            }
        }

        @Override
        public String settingRemove(String name) {
            try {
                ps4.setString(1, name);
                ResultSet rs = ps4.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.SETTING_REMOVE", e);
            }
        }

        @Override
        public String readTranslations(String language) {
            try {
                ps5.setString(1, language);
                ResultSet rs = ps5.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.READ_TRANSLATIONS", e);
            }
        }

        @Override
        public String login(String url, String user, String password) {
            try {
                ps6.setString(1, url);
                ps6.setString(2, user);
                ps6.setString(3, password);
                ResultSet rs = ps6.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.LOGIN", e);
            }
        }

        @Override
        public String testConnection() {
            try {
                ResultSet rs = ps7.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("OPS_SERVICE.TEST_CONNECTION", e);
            }
        }
    }
}
