package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.OrderStatus.OrderStatusDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PDate;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ORDER_STATUS'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = OrderStatusDeserializer.class)
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
        super.setRoot(this);

        this.orderid = new PInteger<>("ORDERID", this);
        this.orderItemid = new PInteger<>("ORDER_ITEMID", this);
        this.timestamp = new PDate<>("TIMESTAMP", this);
        this.status = new PString<>("STATUS", this);
        super.setModelProperties(new ModelProperty[] { this.orderid, this.orderItemid, this.timestamp, this.status });
    }

    @Override
    protected OrderStatus newInstance(ModelTable t, short modelType) {
        return new OrderStatus(t, modelType);
    }

    static class OrderStatusDeserializer extends ModelDeserializer<OrderStatus> {
        @Override
        protected Model<OrderStatus> newModelInstance() {
            return new OrderStatus();
        }
    }
}
