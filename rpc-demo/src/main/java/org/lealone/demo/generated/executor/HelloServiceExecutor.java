package org.lealone.demo.generated.executor;

import org.lealone.db.service.ServiceExecutor;
import org.lealone.demo.HelloServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'hello_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class HelloServiceExecutor implements ServiceExecutor {

    private final HelloServiceImpl s = new HelloServiceImpl();

    public HelloServiceExecutor() {
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "SAY_HELLO":
            ja = new JsonArray(json);
            String p_name1 = ja.getString(0);
            String result1 = this.s.sayHello(p_name1);
            if (result1 == null)
                return null;
            return result1;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
