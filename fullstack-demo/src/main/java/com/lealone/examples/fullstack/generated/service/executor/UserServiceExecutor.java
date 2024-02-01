package com.lealone.examples.fullstack.generated.service.executor;

import com.lealone.db.service.ServiceExecutor;
import com.lealone.db.value.*;
import com.lealone.examples.fullstack.UserServiceImpl;
import com.lealone.examples.fullstack.generated.model.User;
import com.lealone.plugins.orm.json.JsonArray;
import java.util.Map;

/**
 * Service executor for 'user_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class UserServiceExecutor implements ServiceExecutor {

    private final UserServiceImpl si = new UserServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "ADD_USER":
            String p_name_1 = methodArgs[0].getString();
            Integer p_age_1 = methodArgs[1].getInt();
            Long result1 = si.addUser(p_name_1, p_age_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueLong.get(result1);
        case "FIND_BY_NAME":
            String p_name_2 = methodArgs[0].getString();
            User result2 = si.findByName(p_name_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2.encode());
        default:
            throw noMethodException(methodName);
        }
    }

    @Override
    public Object executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "ADD_USER":
            String p_name_1 = toString("NAME", methodArgs);
            Integer p_age_1 = toInt("AGE", methodArgs);
            return si.addUser(p_name_1, p_age_1);
        case "FIND_BY_NAME":
            String p_name_2 = toString("NAME", methodArgs);
            return si.findByName(p_name_2);
        default:
            throw noMethodException(methodName);
        }
    }

    @Override
    public Object executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "ADD_USER":
            ja = new JsonArray(json);
            String p_name_1 = ja.getString(0);
            Integer p_age_1 = Integer.valueOf(ja.getValue(1).toString());
            return si.addUser(p_name_1, p_age_1);
        case "FIND_BY_NAME":
            ja = new JsonArray(json);
            String p_name_2 = ja.getString(0);
            return si.findByName(p_name_2);
        default:
            throw noMethodException(methodName);
        }
    }
}
