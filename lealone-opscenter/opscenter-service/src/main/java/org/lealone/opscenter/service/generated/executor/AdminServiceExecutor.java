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
        case "LOGOUT":
            String result2 = this.s.logout();
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        case "SAVE":
            String p_port_3 = methodArgs[0].getString();
            String p_allowOthers_3 = methodArgs[1].getString();
            String p_ssl_3 = methodArgs[2].getString();
            String result3 = this.s.save(p_port_3, p_allowOthers_3, p_ssl_3);
            if (result3 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result3);
        case "ADMIN":
            String result4 = this.s.admin();
            if (result4 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result4);
        case "START_TRANSLATE":
            String result5 = this.s.startTranslate();
            if (result5 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result5);
        case "SHUTDOWN":
            String result6 = this.s.shutdown();
            if (result6 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result6);
        case "TOOLS":
            String p_tool_7 = methodArgs[0].getString();
            String p_args_7 = methodArgs[1].getString();
            String result7 = this.s.tools(p_tool_7, p_args_7);
            if (result7 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result7);
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
        case "LOGOUT":
            String result2 = this.s.logout();
            if (result2 == null)
                return null;
            return result2;
        case "SAVE":
            String p_port_3 = ServiceExecutor.toString("PORT", methodArgs);
            String p_allowOthers_3 = ServiceExecutor.toString("ALLOW_OTHERS", methodArgs);
            String p_ssl_3 = ServiceExecutor.toString("SSL", methodArgs);
            String result3 = this.s.save(p_port_3, p_allowOthers_3, p_ssl_3);
            if (result3 == null)
                return null;
            return result3;
        case "ADMIN":
            String result4 = this.s.admin();
            if (result4 == null)
                return null;
            return result4;
        case "START_TRANSLATE":
            String result5 = this.s.startTranslate();
            if (result5 == null)
                return null;
            return result5;
        case "SHUTDOWN":
            String result6 = this.s.shutdown();
            if (result6 == null)
                return null;
            return result6;
        case "TOOLS":
            String p_tool_7 = ServiceExecutor.toString("TOOL", methodArgs);
            String p_args_7 = ServiceExecutor.toString("ARGS", methodArgs);
            String result7 = this.s.tools(p_tool_7, p_args_7);
            if (result7 == null)
                return null;
            return result7;
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
        case "LOGOUT":
            String result2 = this.s.logout();
            if (result2 == null)
                return null;
            return result2;
        case "SAVE":
            ja = new JsonArray(json);
            String p_port_3 = ja.getString(0);
            String p_allowOthers_3 = ja.getString(1);
            String p_ssl_3 = ja.getString(2);
            String result3 = this.s.save(p_port_3, p_allowOthers_3, p_ssl_3);
            if (result3 == null)
                return null;
            return result3;
        case "ADMIN":
            String result4 = this.s.admin();
            if (result4 == null)
                return null;
            return result4;
        case "START_TRANSLATE":
            String result5 = this.s.startTranslate();
            if (result5 == null)
                return null;
            return result5;
        case "SHUTDOWN":
            String result6 = this.s.shutdown();
            if (result6 == null)
                return null;
            return result6;
        case "TOOLS":
            ja = new JsonArray(json);
            String p_tool_7 = ja.getString(0);
            String p_args_7 = ja.getString(1);
            String result7 = this.s.tools(p_tool_7, p_args_7);
            if (result7 == null)
                return null;
            return result7;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
