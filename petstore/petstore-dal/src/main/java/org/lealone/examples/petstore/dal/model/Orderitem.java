package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Orderitem.OrderitemDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PBigDecimal;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ORDERITEM'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = OrderitemDeserializer.class)
public class Orderitem extends Model<Orderitem> {

    public static final Orderitem dao = new Orderitem(null, ROOT_DAO);

    public final PInteger<Orderitem> orderid;
    public final PInteger<Orderitem> orderitemid;
    public final PString<Orderitem> itemid;
    public final PInteger<Orderitem> quantity;
    public final PBigDecimal<Orderitem> unitprice;

    public Orderitem() {
        this(null, REGULAR_MODEL);
    }

    private Orderitem(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ORDERITEM") : t, modelType);
        super.setRoot(this);

        this.orderid = new PInteger<>("ORDERID", this);
        this.orderitemid = new PInteger<>("ORDERITEMID", this);
        this.itemid = new PString<>("ITEMID", this);
        this.quantity = new PInteger<>("QUANTITY", this);
        this.unitprice = new PBigDecimal<>("UNITPRICE", this);
        super.setModelProperties(new ModelProperty[] { this.orderid, this.orderitemid, this.itemid, this.quantity, this.unitprice });
    }

    @Override
    protected Orderitem newInstance(ModelTable t, short modelType) {
        return new Orderitem(t, modelType);
    }

    static class OrderitemDeserializer extends ModelDeserializer<Orderitem> {
        @Override
        protected Model<Orderitem> newModelInstance() {
            return new Orderitem();
        }
    }
}
