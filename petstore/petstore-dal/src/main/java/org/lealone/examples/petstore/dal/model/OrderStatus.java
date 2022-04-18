package org.lealone.examples.petstore.dal.model;

import org.lealone.orm.Model;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PDate;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ORDER_STATUS'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class OrderStatus extends Model<OrderStatus> {

    public static final OrderStatus dao = new OrderStatus(null, ROOT_DAO);

    public final PInteger<OrderStatus> orderid;
    public final PInteger<OrderStatus> orderItemid;
    public final PDate<OrderStatus> timestamp;
    public final PString<OrderStatus> status;

    public OrderStatus() {
        this(null, REGULAR_MODEL);
    }

    private OrderStatus(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ORDER_STATUS") : t, modelType);
        orderid = new PInteger<>("ORDERID", this);
        orderItemid = new PInteger<>("ORDER_ITEMID", this);
        timestamp = new PDate<>("TIMESTAMP", this);
        status = new PString<>("STATUS", this);
        super.setModelProperties(new ModelProperty[] { orderid, orderItemid, timestamp, status });
    }

    @Override
    protected OrderStatus newInstance(ModelTable t, short modelType) {
        return new OrderStatus(t, modelType);
    }

    public static OrderStatus decode(String str) {
        return new OrderStatus().decode0(str);
    }
}
