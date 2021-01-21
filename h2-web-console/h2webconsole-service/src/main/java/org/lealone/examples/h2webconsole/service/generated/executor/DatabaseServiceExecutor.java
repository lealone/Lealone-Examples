package org.lealone.examples.h2webconsole.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.h2webconsole.service.DatabaseServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'database_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class DatabaseServiceExecutor implements ServiceExecutor {

    private final DatabaseServiceImpl s = new DatabaseServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "READ_ALL_DATABASE_OBJECTS":
            String p_jsessionid_1 = methodArgs[0].getString();
            String result1 = this.s.readAllDatabaseObjects(p_jsessionid_1);
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
        case "READ_ALL_DATABASE_OBJECTS":
            String p_jsessionid_1 = ServiceExecutor.toString("JSESSIONID", methodArgs);
            String result1 = this.s.readAllDatabaseObjects(p_jsessionid_1);
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
        case "READ_ALL_DATABASE_OBJECTS":
            ja = new JsonArray(json);
            String p_jsessionid_1 = ja.getString(0);
            String result1 = this.s.readAllDatabaseObjects(p_jsessionid_1);
            if (result1 == null)
                return null;
            return result1;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
