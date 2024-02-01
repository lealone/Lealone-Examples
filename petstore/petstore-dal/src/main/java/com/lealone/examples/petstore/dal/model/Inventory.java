package com.lealone.examples.petstore.dal.model;

import com.lealone.plugins.orm.Model;
import com.lealone.plugins.orm.ModelProperty;
import com.lealone.plugins.orm.ModelTable;
import com.lealone.plugins.orm.format.JsonFormat;
import com.lealone.plugins.orm.property.PInteger;
import com.lealone.plugins.orm.property.PString;

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
        return decode(str, null);
    }

    public static Inventory decode(String str, JsonFormat format) {
        return new Inventory().decode0(str, format);
    }
}
