package org.lealone.examples.rpc.generated;

import java.sql.*;
import org.lealone.client.ClientServiceProxy;

/**
 * Service interface for 'hello_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface HelloService {

    static HelloService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.examples.rpc.HelloServiceImpl();
        else
            return new ServiceProxy(url);
    }

    String sayHello(String name);

    static class ServiceProxy implements HelloService {

        private final PreparedStatement ps1;

        private ServiceProxy(String url) {
            ps1 = ClientServiceProxy.prepareStatement(url, "EXECUTE SERVICE HELLO_SERVICE SAY_HELLO(?)");
        }

        @Override
        public String sayHello(String name) {
            try {
                ps1.setString(1, name);
                ResultSet rs = ps1.executeQuery();
                rs.next();
                String ret =  rs.getString(1);
                rs.close();
                return ret;
            } catch (Throwable e) {
                throw ClientServiceProxy.failed("HELLO_SERVICE.SAY_HELLO", e);
            }
        }
    }
}
