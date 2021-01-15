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
            String p_password_1 = methodArgs[0].getString();
            String result1 = this.s.login(p_password_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        case "SAVE":
            String p_port_2 = methodArgs[0].getString();
            String p_allowOthers_2 = methodArgs[1].getString();
            String p_ssl_2 = methodArgs[2].getString();
            String result2 = this.s.save(p_port_2, p_allowOthers_2, p_ssl_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        case "ADMIN":
            String result3 = this.s.admin();
            if (result3 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result3);
        case "START_TRANSLATE":
            String result4 = this.s.startTranslate();
            if (result4 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result4);
        case "SHUTDOWN":
            String result5 = this.s.shutdown();
            if (result5 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result5);
        case "TOOLS":
            String p_tool_6 = methodArgs[0].getString();
            String p_args_6 = methodArgs[1].getString();
            String result6 = this.s.tools(p_tool_6, p_args_6);
            if (result6 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result6);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "LOGIN":
            String p_password_1 = ServiceExecutor.toString("PASSWORD", methodArgs);
            String result1 = this.s.login(p_password_1);
            if (result1 == null)
                return null;
            return result1;
        case "SAVE":
            String p_port_2 = ServiceExecutor.toString("PORT", methodArgs);
            String p_allowOthers_2 = ServiceExecutor.toString("ALLOW_OTHERS", methodArgs);
            String p_ssl_2 = ServiceExecutor.toString("SSL", methodArgs);
            String result2 = this.s.save(p_port_2, p_allowOthers_2, p_ssl_2);
            if (result2 == null)
                return null;
            return result2;
        case "ADMIN":
            String result3 = this.s.admin();
            if (result3 == null)
                return null;
            return result3;
        case "START_TRANSLATE":
            String result4 = this.s.startTranslate();
            if (result4 == null)
                return null;
            return result4;
        case "SHUTDOWN":
            String result5 = this.s.shutdown();
            if (result5 == null)
                return null;
            return result5;
        case "TOOLS":
            String p_tool_6 = ServiceExecutor.toString("TOOL", methodArgs);
            String p_args_6 = ServiceExecutor.toString("ARGS", methodArgs);
            String result6 = this.s.tools(p_tool_6, p_args_6);
            if (result6 == null)
                return null;
            return result6;
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
            String p_password_1 = ja.getString(0);
            String result1 = this.s.login(p_password_1);
            if (result1 == null)
                return null;
            return result1;
        case "SAVE":
            ja = new JsonArray(json);
            String p_port_2 = ja.getString(0);
            String p_allowOthers_2 = ja.getString(1);
            String p_ssl_2 = ja.getString(2);
            String result2 = this.s.save(p_port_2, p_allowOthers_2, p_ssl_2);
            if (result2 == null)
                return null;
            return result2;
        case "ADMIN":
            String result3 = this.s.admin();
            if (result3 == null)
                return null;
            return result3;
        case "START_TRANSLATE":
            String result4 = this.s.startTranslate();
            if (result4 == null)
                return null;
            return result4;
        case "SHUTDOWN":
            String result5 = this.s.shutdown();
            if (result5 == null)
                return null;
            return result5;
        case "TOOLS":
            ja = new JsonArray(json);
            String p_tool_6 = ja.getString(0);
            String p_args_6 = ja.getString(1);
            String result6 = this.s.tools(p_tool_6, p_args_6);
            if (result6 == null)
                return null;
            return result6;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
