package org.lealone.examples.petstore.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.petstore.dal.model.Account;
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
        case "LOGOUT":
            String p_userId_2 = methodArgs[0].getString();
            String result2 = this.s.logout(p_userId_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        case "REGISTER":
            String p_userId_3 = methodArgs[0].getString();
            String p_password_3 = methodArgs[1].getString();
            String p_password2_3 = methodArgs[2].getString();
            this.s.register(p_userId_3, p_password_3, p_password2_3);
            return ValueNull.INSTANCE;
        case "UPDATE":
            Account p_account_4 =  new JsonObject(methodArgs[0].getString()).mapTo(Account.class);
            this.s.update(p_account_4);
            return ValueNull.INSTANCE;
        case "GET_USER":
            String p_userId_5 = methodArgs[0].getString();
            String result5 = this.s.getUser(p_userId_5);
            if (result5 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result5);
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
        case "LOGOUT":
            String p_userId_2 = ServiceExecutor.toString("USER_ID", methodArgs);
            String result2 = this.s.logout(p_userId_2);
            if (result2 == null)
                return null;
            return result2;
        case "REGISTER":
            String p_userId_3 = ServiceExecutor.toString("USER_ID", methodArgs);
            String p_password_3 = ServiceExecutor.toString("PASSWORD", methodArgs);
            String p_password2_3 = ServiceExecutor.toString("PASSWORD2", methodArgs);
            this.s.register(p_userId_3, p_password_3, p_password2_3);
            return NO_RETURN_VALUE;
        case "UPDATE":
            Account p_account_4 =  new JsonObject(ServiceExecutor.toString("ACCOUNT", methodArgs)).mapTo(Account.class);
            this.s.update(p_account_4);
            return NO_RETURN_VALUE;
        case "GET_USER":
            String p_userId_5 = ServiceExecutor.toString("USER_ID", methodArgs);
            String result5 = this.s.getUser(p_userId_5);
            if (result5 == null)
                return null;
            return result5;
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
        case "LOGOUT":
            ja = new JsonArray(json);
            String p_userId_2 = ja.getString(0);
            String result2 = this.s.logout(p_userId_2);
            if (result2 == null)
                return null;
            return result2;
        case "REGISTER":
            ja = new JsonArray(json);
            String p_userId_3 = ja.getString(0);
            String p_password_3 = ja.getString(1);
            String p_password2_3 = ja.getString(2);
            this.s.register(p_userId_3, p_password_3, p_password2_3);
            return NO_RETURN_VALUE;
        case "UPDATE":
            ja = new JsonArray(json);
            Account p_account_4 = ja.getJsonObject(0).mapTo(Account.class);
            this.s.update(p_account_4);
            return NO_RETURN_VALUE;
        case "GET_USER":
            ja = new JsonArray(json);
            String p_userId_5 = ja.getString(0);
            String result5 = this.s.getUser(p_userId_5);
            if (result5 == null)
                return null;
            return result5;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
