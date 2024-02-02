package com.lealone.examples.orm.generated;

import com.lealone.plugins.orm.Model;
import com.lealone.plugins.orm.ModelProperty;
import com.lealone.plugins.orm.ModelTable;
import com.lealone.plugins.orm.format.JsonFormat;
import com.lealone.plugins.orm.property.PInteger;
import com.lealone.plugins.orm.property.PLong;
import com.lealone.plugins.orm.property.PString;
import java.util.List;

/**
 * Model for table 'CUSTOMER'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Customer extends Model<Customer> {

    public static final Customer dao = new Customer(null, ROOT_DAO);

    public final PLong<Customer> id;
    public final PString<Customer> name;
    public final PString<Customer> notes;
    public final PInteger<Customer> phone;

    public Customer() {
        this(null, REGULAR_MODEL);
    }

    private Customer(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("TEST", "PUBLIC", "CUSTOMER") : t, modelType);
        id = new PLong<>("ID", this);
        name = new PString<>("NAME", this);
        notes = new PString<>("NOTES", this);
        phone = new PInteger<>("PHONE", this);
        super.setModelProperties(new ModelProperty[] { id, name, notes, phone });
        super.initAdders(new OrderAdder());
    }

    @Override
    protected Customer newInstance(ModelTable t, short modelType) {
        return new Customer(t, modelType);
    }

    public Customer addOrder(Order m) {
        m.setCustomer(this);
        super.addModel(m);
        return this;
    }

    public Customer addOrder(Order... mArray) {
        for (Order m : mArray)
            addOrder(m);
        return this;
    }

    public List<Order> getOrderList() {
        return super.getModelList(Order.class);
    }

    protected class OrderAdder implements AssociateAdder<Order> {
        @Override
        public Order getDao() {
            return Order.dao;
        }

        @Override
        public void add(Order m) {
            if (areEqual(id, m.customerId)) {
                addOrder(m);
            }
        }
    }

    public static Customer decode(String str) {
        return decode(str, null);
    }

    public static Customer decode(String str, JsonFormat format) {
        return new Customer().decode0(str, format);
    }
}
