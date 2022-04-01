package org.lealone.examples.petstore.dal.model;

import org.lealone.orm.Model;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PBigDecimal;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ITEM'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Item extends Model<Item> {

    public static final Item dao = new Item(null, ROOT_DAO);

    public final PString<Item> itemid;
    public final PString<Item> productid;
    public final PBigDecimal<Item> listprice;
    public final PBigDecimal<Item> unitcost;
    public final PInteger<Item> supplierid;
    public final PString<Item> status;
    public final PString<Item> attr1;
    public final PString<Item> attr2;
    public final PString<Item> attr3;
    public final PString<Item> attr4;
    public final PString<Item> attr5;
    private Product product;
    private Supplier supplier;

    public Item() {
        this(null, REGULAR_MODEL);
    }

    private Item(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ITEM") : t, modelType);
        super.setRoot(this);

        this.itemid = new PString<>("ITEMID", this);
        this.productid = new PString<>("PRODUCTID", this);
        this.listprice = new PBigDecimal<>("LISTPRICE", this);
        this.unitcost = new PBigDecimal<>("UNITCOST", this);
        this.supplierid = new PInteger<>("SUPPLIERID", this);
        this.status = new PString<>("STATUS", this);
        this.attr1 = new PString<>("ATTR1", this);
        this.attr2 = new PString<>("ATTR2", this);
        this.attr3 = new PString<>("ATTR3", this);
        this.attr4 = new PString<>("ATTR4", this);
        this.attr5 = new PString<>("ATTR5", this);
        super.setModelProperties(new ModelProperty[] { this.itemid, this.productid, this.listprice, this.unitcost, this.supplierid, this.status, this.attr1, this.attr2, this.attr3, this.attr4, this.attr5 });
    }

    public Product getProduct() {
        return product;
    }

    public Item setProduct(Product product) {
        this.product = product;
        this.productid.set(product.productid.get());
        return this;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Item setSupplier(Supplier supplier) {
        this.supplier = supplier;
        this.supplierid.set(supplier.suppid.get());
        return this;
    }

    @Override
    protected Item newInstance(ModelTable t, short modelType) {
        return new Item(t, modelType);
    }

    public static Item decode(String str) {
        return new Item().decode0(str);
    }
}
