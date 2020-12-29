package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.OrderItem.OrderItemDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PBigDecimal;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ORDER_ITEM'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = OrderItemDeserializer.class)
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
        super.setRoot(this);

        this.orderid = new PInteger<>("ORDERID", this);
        this.orderItemid = new PInteger<>("ORDER_ITEMID", this);
        this.itemid = new PString<>("ITEMID", this);
        this.quantity = new PInteger<>("QUANTITY", this);
        this.unitprice = new PBigDecimal<>("UNITPRICE", this);
        super.setModelProperties(new ModelProperty[] { this.orderid, this.orderItemid, this.itemid, this.quantity, this.unitprice });
    }

    @Override
    protected OrderItem newInstance(ModelTable t, short modelType) {
        return new OrderItem(t, modelType);
    }

    static class OrderItemDeserializer extends ModelDeserializer<OrderItem> {
        @Override
        protected Model<OrderItem> newModelInstance() {
            return new OrderItem();
        }
    }
}
