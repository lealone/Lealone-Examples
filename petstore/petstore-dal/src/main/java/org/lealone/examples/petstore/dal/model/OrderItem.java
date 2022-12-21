package org.lealone.examples.petstore.dal.model;

import org.lealone.plugins.orm.Model;
import org.lealone.plugins.orm.ModelProperty;
import org.lealone.plugins.orm.ModelTable;
import org.lealone.plugins.orm.format.JsonFormat;
import org.lealone.plugins.orm.property.PBigDecimal;
import org.lealone.plugins.orm.property.PInteger;
import org.lealone.plugins.orm.property.PString;

/**
 * Model for table 'ORDER_ITEM'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class OrderItem extends Model<OrderItem> {

    public static final OrderItem dao = new OrderItem(null, ROOT_DAO);

    public final PInteger<OrderItem> orderid;
    public final PInteger<OrderItem> orderItemid;
    public final PString<OrderItem> itemid;
    public final PInteger<OrderItem> quantity;
    public final PBigDecimal<OrderItem> unitprice;

    public OrderItem() {
        this(null, REGULAR_MODEL);
    }

    private OrderItem(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ORDER_ITEM") : t, modelType);
        orderid = new PInteger<>("ORDERID", this);
        orderItemid = new PInteger<>("ORDER_ITEMID", this);
        itemid = new PString<>("ITEMID", this);
        quantity = new PInteger<>("QUANTITY", this);
        unitprice = new PBigDecimal<>("UNITPRICE", this);
        super.setModelProperties(new ModelProperty[] { orderid, orderItemid, itemid, quantity, unitprice });
    }

    @Override
    protected OrderItem newInstance(ModelTable t, short modelType) {
        return new OrderItem(t, modelType);
    }

    public static OrderItem decode(String str) {
        return decode(str, null);
    }

    public static OrderItem decode(String str, JsonFormat format) {
        return new OrderItem().decode0(str, format);
    }
}
