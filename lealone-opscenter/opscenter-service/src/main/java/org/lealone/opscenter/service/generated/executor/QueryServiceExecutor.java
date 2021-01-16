package org.lealone.opscenter.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.opscenter.service.QueryServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'query_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class QueryServiceExecutor implements ServiceExecutor {

    private final QueryServiceImpl s = new QueryServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "QUERY":
            String p_sql_1 = methodArgs[0].getString();
            String result1 = this.s.query(p_sql_1);
            if (result1 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result1);
        case "EDIT_RESULT":
            Integer p_row_2 = methodArgs[0].getInt();
            Integer p_op_2 = methodArgs[1].getInt();
            String p_value_2 = methodArgs[2].getString();
            String result2 = this.s.editResult(p_row_2, p_op_2, p_value_2);
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
        case "QUERY":
            String p_sql_1 = ServiceExecutor.toString("SQL", methodArgs);
            String result1 = this.s.query(p_sql_1);
            if (result1 == null)
                return null;
            return result1;
        case "EDIT_RESULT":
            Integer p_row_2 = Integer.valueOf(ServiceExecutor.toString("ROW", methodArgs));
            Integer p_op_2 = Integer.valueOf(ServiceExecutor.toString("OP", methodArgs));
            String p_value_2 = ServiceExecutor.toString("VALUE", methodArgs);
            String result2 = this.s.editResult(p_row_2, p_op_2, p_value_2);
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
        case "QUERY":
            ja = new JsonArray(json);
            String p_sql_1 = ja.getString(0);
            String result1 = this.s.query(p_sql_1);
            if (result1 == null)
                return null;
            return result1;
        case "EDIT_RESULT":
            ja = new JsonArray(json);
            Integer p_row_2 = Integer.valueOf(ja.getValue(0).toString());
            Integer p_op_2 = Integer.valueOf(ja.getValue(1).toString());
            String p_value_2 = ja.getString(2);
            String result2 = this.s.editResult(p_row_2, p_op_2, p_value_2);
            if (result2 == null)
                return null;
            return result2;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
