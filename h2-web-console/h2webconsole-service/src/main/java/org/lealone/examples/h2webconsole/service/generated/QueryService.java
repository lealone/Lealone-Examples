package org.lealone.examples.h2webconsole.service.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'query_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface QueryService {

    static QueryService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.examples.h2webconsole.service.QueryServiceImpl();
        else
            return new ServiceProxy(url);
    }

    String query(String jsessionid, String sql);

    String editResult(String jsessionid, Integer row, Integer op, String value);

    static class ServiceProxy implements QueryService {

        private final PreparedStatement ps1;
        private final PreparedStatement ps2;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE QUERY_SERVICE QUERY(?, ?)");
            ps2 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE QUERY_SERVICE EDIT_RESULT(?, ?, ?, ?)");
        }

        @Override
        public String query(String jsessionid, String sql) {
            try {
                ps1.setString(1, jsessionid);
                ps1.setString(2, sql);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("QUERY_SERVICE.QUERY", e);
            }
        }

        @Override
        public String editResult(String jsessionid, Integer row, Integer op, String value) {
            try {
                ps2.setString(1, jsessionid);
                ps2.setInt(2, row);
                ps2.setInt(3, op);
                ps2.setString(4, value);
                ResultSet rs = ps2.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("QUERY_SERVICE.EDIT_RESULT", e);
            }
        }
    }
}
