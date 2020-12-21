package org.lealone.examples.fullstack.generated.service.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.fullstack.UserServiceImpl;
import org.lealone.examples.fullstack.generated.model.User;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

/**
 * Service executor for 'user_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class UserServiceExecutor implements ServiceExecutor {

    private final UserServiceImpl s = new UserServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "ADD_USER":
            String p_name_1 = methodArgs[0].getString();
            Integer p_age_1 = methodArgs[1].getInt();
            Long result1 = this.s.addUser(p_name_1, p_age_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueLong.get(result1);
        case "FIND_BY_NAME":
            String p_name_2 = methodArgs[0].getString();
            User result2 = this.s.findByName(p_name_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(JsonObject.mapFrom(result2).encode());
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "ADD_USER":
            String p_name_1 = ServiceExecutor.toString("NAME", methodArgs);
            Integer p_age_1 = Integer.valueOf(ServiceExecutor.toString("AGE", methodArgs));
            Long result1 = this.s.addUser(p_name_1, p_age_1);
            if (result1 == null)
                return null;
            return result1.toString();
        case "FIND_BY_NAME":
            String p_name_2 = ServiceExecutor.toString("NAME", methodArgs);
            User result2 = this.s.findByName(p_name_2);
            if (result2 == null)
                return null;
            return JsonObject.mapFrom(result2).encode();
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "ADD_USER":
            ja = new JsonArray(json);
            String p_name_1 = ja.getString(0);
            Integer p_age_1 = Integer.valueOf(ja.getValue(1).toString());
            Long result1 = this.s.addUser(p_name_1, p_age_1);
            if (result1 == null)
                return null;
            return result1.toString();
        case "FIND_BY_NAME":
            ja = new JsonArray(json);
            String p_name_2 = ja.getString(0);
            User result2 = this.s.findByName(p_name_2);
            if (result2 == null)
                return null;
            return JsonObject.mapFrom(result2).encode();
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
