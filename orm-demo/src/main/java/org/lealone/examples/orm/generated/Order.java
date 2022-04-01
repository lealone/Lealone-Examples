package org.lealone.examples.orm.generated;

import org.lealone.orm.Model;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PDate;
import org.lealone.orm.property.PDouble;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PLong;

/**
 * Model for table 'ORDER'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Order extends Model<Order> {

    public static final Order dao = new Order(null, ROOT_DAO);

    public final PLong<Order> customerId;
    public final PInteger<Order> orderId;
    public final PDate<Order> orderDate;
    public final PDouble<Order> total;
    private Customer customer;

    public Order() {
        this(null, REGULAR_MODEL);
    }

    private Order(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("TEST", "PUBLIC", "ORDER") : t, modelType);
        super.setRoot(this);

        this.customerId = new PLong<>("CUSTOMER_ID", this);
        this.orderId = new PInteger<>("ORDER_ID", this);
        this.orderDate = new PDate<>("ORDER_DATE", this);
        this.total = new PDouble<>("TOTAL", this);
        super.setModelProperties(new ModelProperty[] { this.customerId, this.orderId, this.orderDate, this.total });
    }

    public Customer getCustomer() {
        return customer;
    }

    public Order setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId.set(customer.id.get());
        return this;
    }

    @Override
    protected Order newInstance(ModelTable t, short modelType) {
        return new Order(t, modelType);
    }

    public static Order decode(String str) {
        return new Order().decode0(str);
    }
}
