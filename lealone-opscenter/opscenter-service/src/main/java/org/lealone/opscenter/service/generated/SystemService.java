package org.lealone.opscenter.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'system_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface SystemService {

    static SystemService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.opscenter.service.SystemServiceImpl();
        else
            return new ServiceProxy(url);
    }

    String loadServices(String serviceNames);

    static class ServiceProxy implements SystemService {

        private final PreparedStatement ps1;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE SYSTEM_SERVICE LOAD_SERVICES(?)");
        }

        @Override
        public String loadServices(String serviceNames) {
            try {
                ps1.setString(1, serviceNames);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("SYSTEM_SERVICE.LOAD_SERVICES", e);
            }
        }
    }
}
