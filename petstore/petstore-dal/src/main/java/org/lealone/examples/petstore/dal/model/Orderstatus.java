package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Orderstatus.OrderstatusDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PDate;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ORDERSTATUS'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = OrderstatusDeserializer.class)
public class Orderstatus extends Model<Orderstatus> {

    public static final Orderstatus dao = new Orderstatus(null, ROOT_DAO);

    public final PInteger<Orderstatus> orderid;
    public final PInteger<Orderstatus> orderitemid;
    public final PDate<Orderstatus> timestamp;
    public final PString<Orderstatus> status;

    public Orderstatus() {
        this(null, REGULAR_MODEL);
    }

    private Orderstatus(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ORDERSTATUS") : t, modelType);
        super.setRoot(this);

        this.orderid = new PInteger<>("ORDERID", this);
        this.orderitemid = new PInteger<>("ORDERITEMID", this);
        this.timestamp = new PDate<>("TIMESTAMP", this);
        this.status = new PString<>("STATUS", this);
        super.setModelProperties(new ModelProperty[] { this.orderid, this.orderitemid, this.timestamp, this.status });
    }

    @Override
    protected Orderstatus newInstance(ModelTable t, short modelType) {
        return new Orderstatus(t, modelType);
    }

    static class OrderstatusDeserializer extends ModelDeserializer<Orderstatus> {
        @Override
        protected Model<Orderstatus> newModelInstance() {
            return new Orderstatus();
        }
    }
}
