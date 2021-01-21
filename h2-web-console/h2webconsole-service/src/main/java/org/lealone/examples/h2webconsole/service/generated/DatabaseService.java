package org.lealone.examples.h2webconsole.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'database_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface DatabaseService {

    static DatabaseService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.examples.h2webconsole.service.DatabaseServiceImpl();
        else
            return new ServiceProxy(url);
    }

    String readAllDatabaseObjects(String jsessionid);

    static class ServiceProxy implements DatabaseService {

        private final PreparedStatement ps1;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE DATABASE_SERVICE READ_ALL_DATABASE_OBJECTS(?)");
        }

        @Override
        public String readAllDatabaseObjects(String jsessionid) {
            try {
                ps1.setString(1, jsessionid);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("DATABASE_SERVICE.READ_ALL_DATABASE_OBJECTS", e);
            }
        }
    }
}
