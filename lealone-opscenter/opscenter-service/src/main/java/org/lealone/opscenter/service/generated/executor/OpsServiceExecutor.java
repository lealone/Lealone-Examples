package org.lealone.opscenter.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.opscenter.service.OpsServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'ops_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class OpsServiceExecutor implements ServiceExecutor {

    private final OpsServiceImpl s = new OpsServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "GET_LANGUAGES":
            String result1 = this.s.getLanguages();
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        case "GET_SETTINGS":
            String p_setting_2 = methodArgs[0].getString();
            String result2 = this.s.getSettings(p_setting_2);
            if (result2 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result2);
        case "SETTING_SAVE":
            String p_name_3 = methodArgs[0].getString();
            String p_driver_3 = methodArgs[1].getString();
            String p_url_3 = methodArgs[2].getString();
            String p_user_3 = methodArgs[3].getString();
            String result3 = this.s.settingSave(p_name_3, p_driver_3, p_url_3, p_user_3);
            if (result3 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result3);
        case "SETTING_REMOVE":
            String p_name_4 = methodArgs[0].getString();
            String result4 = this.s.settingRemove(p_name_4);
            if (result4 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result4);
        case "READ_TRANSLATIONS":
            String p_language_5 = methodArgs[0].getString();
            String result5 = this.s.readTranslations(p_language_5);
            if (result5 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result5);
        case "LOGIN":
            String p_url_6 = methodArgs[0].getString();
            String p_user_6 = methodArgs[1].getString();
            String p_password_6 = methodArgs[2].getString();
            String result6 = this.s.login(p_url_6, p_user_6, p_password_6);
            if (result6 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result6);
        case "TEST_CONNECTION":
            String result7 = this.s.testConnection();
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
        case "GET_LANGUAGES":
            String result1 = this.s.getLanguages();
            if (result1 == null)
                return null;
            return result1;
        case "GET_SETTINGS":
            String p_setting_2 = ServiceExecutor.toString("SETTING", methodArgs);
            String result2 = this.s.getSettings(p_setting_2);
            if (result2 == null)
                return null;
            return result2;
        case "SETTING_SAVE":
            String p_name_3 = ServiceExecutor.toString("NAME", methodArgs);
            String p_driver_3 = ServiceExecutor.toString("DRIVER", methodArgs);
            String p_url_3 = ServiceExecutor.toString("URL", methodArgs);
            String p_user_3 = ServiceExecutor.toString("USER", methodArgs);
            String result3 = this.s.settingSave(p_name_3, p_driver_3, p_url_3, p_user_3);
            if (result3 == null)
                return null;
            return result3;
        case "SETTING_REMOVE":
            String p_name_4 = ServiceExecutor.toString("NAME", methodArgs);
            String result4 = this.s.settingRemove(p_name_4);
            if (result4 == null)
                return null;
            return result4;
        case "READ_TRANSLATIONS":
            String p_language_5 = ServiceExecutor.toString("LANGUAGE", methodArgs);
            String result5 = this.s.readTranslations(p_language_5);
            if (result5 == null)
                return null;
            return result5;
        case "LOGIN":
            String p_url_6 = ServiceExecutor.toString("URL", methodArgs);
            String p_user_6 = ServiceExecutor.toString("USER", methodArgs);
            String p_password_6 = ServiceExecutor.toString("PASSWORD", methodArgs);
            String result6 = this.s.login(p_url_6, p_user_6, p_password_6);
            if (result6 == null)
                return null;
            return result6;
        case "TEST_CONNECTION":
            String result7 = this.s.testConnection();
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
        case "GET_LANGUAGES":
            String result1 = this.s.getLanguages();
            if (result1 == null)
                return null;
            return result1;
        case "GET_SETTINGS":
            ja = new JsonArray(json);
            String p_setting_2 = ja.getString(0);
            String result2 = this.s.getSettings(p_setting_2);
            if (result2 == null)
                return null;
            return result2;
        case "SETTING_SAVE":
            ja = new JsonArray(json);
            String p_name_3 = ja.getString(0);
            String p_driver_3 = ja.getString(1);
            String p_url_3 = ja.getString(2);
            String p_user_3 = ja.getString(3);
            String result3 = this.s.settingSave(p_name_3, p_driver_3, p_url_3, p_user_3);
            if (result3 == null)
                return null;
            return result3;
        case "SETTING_REMOVE":
            ja = new JsonArray(json);
            String p_name_4 = ja.getString(0);
            String result4 = this.s.settingRemove(p_name_4);
            if (result4 == null)
                return null;
            return result4;
        case "READ_TRANSLATIONS":
            ja = new JsonArray(json);
            String p_language_5 = ja.getString(0);
            String result5 = this.s.readTranslations(p_language_5);
            if (result5 == null)
                return null;
            return result5;
        case "LOGIN":
            ja = new JsonArray(json);
            String p_url_6 = ja.getString(0);
            String p_user_6 = ja.getString(1);
            String p_password_6 = ja.getString(2);
            String result6 = this.s.login(p_url_6, p_user_6, p_password_6);
            if (result6 == null)
                return null;
            return result6;
        case "TEST_CONNECTION":
            String result7 = this.s.testConnection();
            if (result7 == null)
                return null;
            return result7;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
