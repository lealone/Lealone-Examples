package org.lealone.opscenter.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.opscenter.service.SystemServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'system_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class SystemServiceExecutor implements ServiceExecutor {

    private final SystemServiceImpl s = new SystemServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "LOAD_SERVICES":
            String p_serviceNames_1 = methodArgs[0].getString();
            String result1 = this.s.loadServices(p_serviceNames_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "LOAD_SERVICES":
            String p_serviceNames_1 = ServiceExecutor.toString("SERVICE_NAMES", methodArgs);
            String result1 = this.s.loadServices(p_serviceNames_1);
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
        case "LOAD_SERVICES":
            ja = new JsonArray(json);
            String p_serviceNames_1 = ja.getString(0);
            String result1 = this.s.loadServices(p_serviceNames_1);
            if (result1 == null)
                return null;
            return result1;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
