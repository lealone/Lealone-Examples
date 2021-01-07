package org.lealone.opscenter.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.opscenter.service.AdminServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'admin_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class AdminServiceExecutor implements ServiceExecutor {

    private final AdminServiceImpl s = new AdminServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "LOGIN":
            String p_username_1 = methodArgs[0].getString();
            String p_password_1 = methodArgs[1].getString();
            String result1 = this.s.login(p_username_1, p_password_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        case "SAVE":
            String result2 = this.s.save();
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "LOGIN":
            String p_username_1 = ServiceExecutor.toString("USERNAME", methodArgs);
            String p_password_1 = ServiceExecutor.toString("PASSWORD", methodArgs);
            String result1 = this.s.login(p_username_1, p_password_1);
            if (result1 == null)
                return null;
            return result1;
        case "SAVE":
            String result2 = this.s.save();
            if (result2 == null)
                return null;
            return result2;
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
            String p_username_1 = ja.getString(0);
            String p_password_1 = ja.getString(1);
            String result1 = this.s.login(p_username_1, p_password_1);
            if (result1 == null)
                return null;
            return result1;
        case "SAVE":
            String result2 = this.s.save();
            if (result2 == null)
                return null;
            return result2;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
