package org.lealone.demo.generated;

import org.lealone.client.ClientServiceProxy;
import org.lealone.orm.json.JsonArray;

/**
 * Service interface for 'hello_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public interface HelloService {

    static HelloService create(String url) {
        if (new org.lealone.db.ConnectionInfo(url).isEmbedded())
            return new org.lealone.demo.HelloServiceImpl();
        else;
            return new Proxy(url);
    }

    String sayHello(String name);

    static class Proxy implements HelloService {

        private final String url;

        private Proxy(String url) {
            this.url = url;
        }

        @Override
        public String sayHello(String name) {
            JsonArray ja = new JsonArray();
            ja.add(name);
            String result = ClientServiceProxy.executeWithReturnValue(url, "HELLO_SERVICE.SAY_HELLO", ja.encode());
            if (result != null) {
                return result;
            }
            return null;
        }
    }
}
