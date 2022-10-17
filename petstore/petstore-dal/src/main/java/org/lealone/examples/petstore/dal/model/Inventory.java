package org.lealone.examples.petstore.dal.model;

import org.lealone.plugins.orm.Model;
import org.lealone.plugins.orm.ModelProperty;
import org.lealone.plugins.orm.ModelTable;
import org.lealone.plugins.orm.property.PInteger;
import org.lealone.plugins.orm.property.PString;

/**
 * Model for table 'INVENTORY'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Inventory extends Model<Inventory> {

    public static final Inventory dao = new Inventory(null, ROOT_DAO);

    public final PString<Inventory> itemid;
    public final PInteger<Inventory> qty;

    public Inventory() {
        this(null, REGULAR_MODEL);
    }

    private Inventory(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "INVENTORY") : t, modelType);
        itemid = new PString<>("ITEMID", this);
        qty = new PInteger<>("QTY", this);
        super.setModelProperties(new ModelProperty[] { itemid, qty });
    }

    @Override
    protected Inventory newInstance(ModelTable t, short modelType) {
        return new Inventory(t, modelType);
    }

    public static Inventory decode(String str) {
        return new Inventory().decode0(str);
    }
}
