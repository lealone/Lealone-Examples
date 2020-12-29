package org.lealone.examples.petstore.service.generated.executor;

import java.util.Map;
import org.lealone.db.service.ServiceExecutor;
import org.lealone.db.value.*;
import org.lealone.examples.petstore.service.CarServiceImpl;
import org.lealone.orm.json.JsonArray;

/**
 * Service executor for 'car_service'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class CarServiceExecutor implements ServiceExecutor {

    private final CarServiceImpl s = new CarServiceImpl();

    @Override
    public Value executeService(String methodName, Value[] methodArgs) {
        switch (methodName) {
        case "ADD_ITEM":
            String p_carId_1 = methodArgs[0].getString();
            String p_itemId_1 = methodArgs[1].getString();
            this.s.addItem(p_carId_1, p_itemId_1);
            return ValueNull.INSTANCE;
        case "REMOVE_ITEM":
            String p_carId_2 = methodArgs[0].getString();
            String p_itemId_2 = methodArgs[1].getString();
            this.s.removeItem(p_carId_2, p_itemId_2);
            return ValueNull.INSTANCE;
        case "UPDATE":
            String p_carId_3 = methodArgs[0].getString();
            String p_itemId_3 = methodArgs[1].getString();
            Integer p_quantity_3 = methodArgs[2].getInt();
            this.s.update(p_carId_3, p_itemId_3, p_quantity_3);
            return ValueNull.INSTANCE;
        case "GET_ITEMS":
            String p_carId_4 = methodArgs[0].getString();
            String result4 = this.s.getItems(p_carId_4);
            if (result4 == null)
                return ValueNull.INSTANCE;
            return ValueString.get(result4);
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, Map<String, Object> methodArgs) {
        switch (methodName) {
        case "ADD_ITEM":
            String p_carId_1 = ServiceExecutor.toString("CAR_ID", methodArgs);
            String p_itemId_1 = ServiceExecutor.toString("ITEM_ID", methodArgs);
            this.s.addItem(p_carId_1, p_itemId_1);
            return NO_RETURN_VALUE;
        case "REMOVE_ITEM":
            String p_carId_2 = ServiceExecutor.toString("CAR_ID", methodArgs);
            String p_itemId_2 = ServiceExecutor.toString("ITEM_ID", methodArgs);
            this.s.removeItem(p_carId_2, p_itemId_2);
            return NO_RETURN_VALUE;
        case "UPDATE":
            String p_carId_3 = ServiceExecutor.toString("CAR_ID", methodArgs);
            String p_itemId_3 = ServiceExecutor.toString("ITEM_ID", methodArgs);
            Integer p_quantity_3 = Integer.valueOf(ServiceExecutor.toString("QUANTITY", methodArgs));
            this.s.update(p_carId_3, p_itemId_3, p_quantity_3);
            return NO_RETURN_VALUE;
        case "GET_ITEMS":
            String p_carId_4 = ServiceExecutor.toString("CAR_ID", methodArgs);
            String result4 = this.s.getItems(p_carId_4);
            if (result4 == null)
                return null;
            return result4;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }

    @Override
    public String executeService(String methodName, String json) {
        JsonArray ja = null;
        switch (methodName) {
        case "ADD_ITEM":
            ja = new JsonArray(json);
            String p_carId_1 = ja.getString(0);
            String p_itemId_1 = ja.getString(1);
            this.s.addItem(p_carId_1, p_itemId_1);
            return NO_RETURN_VALUE;
        case "REMOVE_ITEM":
            ja = new JsonArray(json);
            String p_carId_2 = ja.getString(0);
            String p_itemId_2 = ja.getString(1);
            this.s.removeItem(p_carId_2, p_itemId_2);
            return NO_RETURN_VALUE;
        case "UPDATE":
            ja = new JsonArray(json);
            String p_carId_3 = ja.getString(0);
            String p_itemId_3 = ja.getString(1);
            Integer p_quantity_3 = Integer.valueOf(ja.getValue(2).toString());
            this.s.update(p_carId_3, p_itemId_3, p_quantity_3);
            return NO_RETURN_VALUE;
        case "GET_ITEMS":
            ja = new JsonArray(json);
            String p_carId_4 = ja.getString(0);
            String result4 = this.s.getItems(p_carId_4);
            if (result4 == null)
                return null;
            return result4;
        default:
            throw new RuntimeException("no method: " + methodName);
        }
    }
}
