package org.lealone.examples.rpc.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.rpc.HelloServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'hello_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class HelloServiceExecutor implements ServiceExecutor {

    private final HelloServiceImpl s = new HelloServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "SAY_HELLO":
            String p_name_1 = methodArgs[0].getString();
            String result1 = this.s.sayHello(p_name_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, String> methodArgs) {
        switch (methodName) {
        case "SAY_HELLO":
            String p_name_1 = methodArgs.get("NAME");
            String result1 = this.s.sayHello(p_name_1);
            if (result1 == null)
                return null;
            return result1;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "SAY_HELLO":
            ja = new JsonArray(json);
            String p_name_1 = ja.getString(0);
            String result1 = this.s.sayHello(p_name_1);
            if (result1 == null)
                return null;
            return result1;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
