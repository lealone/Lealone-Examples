package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Orders.OrdersDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PBigDecimal;
import org.lealone.orm.property.PDate;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ORDERS'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = OrdersDeserializer.class)
public class Orders extends Model<Orders> {

    public static final Orders dao = new Orders(null, ROOT_DAO);

    public final PInteger<Orders> orderid;
    public final PString<Orders> userid;
    public final PDate<Orders> orderdate;
    public final PString<Orders> shipaddr1;
    public final PString<Orders> shipaddr2;
    public final PString<Orders> shipcity;
    public final PString<Orders> shipstate;
    public final PString<Orders> shipzip;
    public final PString<Orders> shipcountry;
    public final PString<Orders> billaddr1;
    public final PString<Orders> billaddr2;
    public final PString<Orders> billcity;
    public final PString<Orders> billstate;
    public final PString<Orders> billzip;
    public final PString<Orders> billcountry;
    public final PString<Orders> courier;
    public final PBigDecimal<Orders> totalprice;
    public final PString<Orders> billtofirstname;
    public final PString<Orders> billtolastname;
    public final PString<Orders> shiptofirstname;
    public final PString<Orders> shiptolastname;
    public final PString<Orders> creditcard;
    public final PString<Orders> exprdate;
    public final PString<Orders> cardtype;
    public final PString<Orders> locale;

    public Orders() {
        this(null, REGULAR_MODEL);
    }

    private Orders(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ORDERS") : t, modelType);
        super.setRoot(this);

        this.orderid = new PInteger<>("ORDERID", this);
        this.userid = new PString<>("USERID", this);
        this.orderdate = new PDate<>("ORDERDATE", this);
        this.shipaddr1 = new PString<>("SHIPADDR1", this);
        this.shipaddr2 = new PString<>("SHIPADDR2", this);
        this.shipcity = new PString<>("SHIPCITY", this);
        this.shipstate = new PString<>("SHIPSTATE", this);
        this.shipzip = new PString<>("SHIPZIP", this);
        this.shipcountry = new PString<>("SHIPCOUNTRY", this);
        this.billaddr1 = new PString<>("BILLADDR1", this);
        this.billaddr2 = new PString<>("BILLADDR2", this);
        this.billcity = new PString<>("BILLCITY", this);
        this.billstate = new PString<>("BILLSTATE", this);
        this.billzip = new PString<>("BILLZIP", this);
        this.billcountry = new PString<>("BILLCOUNTRY", this);
        this.courier = new PString<>("COURIER", this);
        this.totalprice = new PBigDecimal<>("TOTALPRICE", this);
        this.billtofirstname = new PString<>("BILLTOFIRSTNAME", this);
        this.billtolastname = new PString<>("BILLTOLASTNAME", this);
        this.shiptofirstname = new PString<>("SHIPTOFIRSTNAME", this);
        this.shiptolastname = new PString<>("SHIPTOLASTNAME", this);
        this.creditcard = new PString<>("CREDITCARD", this);
        this.exprdate = new PString<>("EXPRDATE", this);
        this.cardtype = new PString<>("CARDTYPE", this);
        this.locale = new PString<>("LOCALE", this);
        super.setModelProperties(new ModelProperty[] { this.orderid, this.userid, this.orderdate, this.shipaddr1, this.shipaddr2, this.shipcity, this.shipstate, this.shipzip, this.shipcountry, this.billaddr1, this.billaddr2, this.billcity, this.billstate, this.billzip, this.billcountry, this.courier, this.totalprice, this.billtofirstname, this.billtolastname, this.shiptofirstname, this.shiptolastname, this.creditcard, this.exprdate, this.cardtype, this.locale });
    }

    @Override
    protected Orders newInstance(ModelTable t, short modelType) {
        return new Orders(t, modelType);
    }

    static class OrdersDeserializer extends ModelDeserializer<Orders> {
        @Override
        protected Model<Orders> newModelInstance() {
            return new Orders();
        }
    }
}
