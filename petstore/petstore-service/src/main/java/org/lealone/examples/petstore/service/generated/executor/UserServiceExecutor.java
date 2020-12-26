package org.lealone.examples.petstore.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.petstore.dal.model.User;
import org.lealone.examples.petstore.service.UserServiceImpl;
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
        case "LOGIN":
            String p_userId_1 = methodArgs[0].getString();
            String p_password_1 = methodArgs[1].getString();
            User result1 = this.s.login(p_userId_1, p_password_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(JsonObject.mapFrom(result1).encode());
        case "REGISTER":
            User p_user_2 =  new JsonObject(methodArgs[0].getString()).mapTo(User.class);
            this.s.register(p_user_2);
            return ValueNull.INSTANCE;
        case "UPDATE":
            User p_user_3 =  new JsonObject(methodArgs[0].getString()).mapTo(User.class);
            this.s.update(p_user_3);
            return ValueNull.INSTANCE;
        case "GET_USER":
            String p_userId_4 = methodArgs[0].getString();
            User result4 = this.s.getUser(p_userId_4);
            if (result4 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(JsonObject.mapFrom(result4).encode());
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "LOGIN":
            String p_userId_1 = ServiceExecutor.toString("USER_ID", methodArgs);
            String p_password_1 = ServiceExecutor.toString("PASSWORD", methodArgs);
            User result1 = this.s.login(p_userId_1, p_password_1);
            if (result1 == null)
                return null;
            return JsonObject.mapFrom(result1).encode();
        case "REGISTER":
            User p_user_2 =  new JsonObject(ServiceExecutor.toString("USER", methodArgs)).mapTo(User.class);
            this.s.register(p_user_2);
            return NO_RETURN_VALUE;
        case "UPDATE":
            User p_user_3 =  new JsonObject(ServiceExecutor.toString("USER", methodArgs)).mapTo(User.class);
            this.s.update(p_user_3);
            return NO_RETURN_VALUE;
        case "GET_USER":
            String p_userId_4 = ServiceExecutor.toString("USER_ID", methodArgs);
            User result4 = this.s.getUser(p_userId_4);
            if (result4 == null)
                return null;
            return JsonObject.mapFrom(result4).encode();
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "LOGIN":
            ja = new JsonArray(json);
            String p_userId_1 = ja.getString(0);
            String p_password_1 = ja.getString(1);
            User result1 = this.s.login(p_userId_1, p_password_1);
            if (result1 == null)
                return null;
            return JsonObject.mapFrom(result1).encode();
        case "REGISTER":
            ja = new JsonArray(json);
            User p_user_2 = ja.getJsonObject(0).mapTo(User.class);
            this.s.register(p_user_2);
            return NO_RETURN_VALUE;
        case "UPDATE":
            ja = new JsonArray(json);
            User p_user_3 = ja.getJsonObject(0).mapTo(User.class);
            this.s.update(p_user_3);
            return NO_RETURN_VALUE;
        case "GET_USER":
            ja = new JsonArray(json);
            String p_userId_4 = ja.getString(0);
            User result4 = this.s.getUser(p_userId_4);
            if (result4 == null)
                return null;
            return JsonObject.mapFrom(result4).encode();
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
