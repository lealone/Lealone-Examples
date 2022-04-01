package org.lealone.examples.petstore.dal.model;

import java.util.ArrayList;
import java.util.List;
import org.lealone.orm.Model;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PString;

/**
 * Model for table 'PRODUCT'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Product extends Model<Product> {

    public static final Product dao = new Product(null, ROOT_DAO);

    public final PString<Product> productid;
    public final PString<Product> categoryid;
    public final PString<Product> name;
    public final PString<Product> logo;
    public final PString<Product> descn;
    private Category category;

    public Product() {
        this(null, REGULAR_MODEL);
    }

    private Product(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "PRODUCT") : t, modelType);
        super.setRoot(this);

        this.productid = new PString<>("PRODUCTID", this);
        this.categoryid = new PString<>("CATEGORYID", this);
        this.name = new PString<>("NAME", this);
        this.logo = new PString<>("LOGO", this);
        this.descn = new PString<>("DESCN", this);
        super.setModelProperties(new ModelProperty[] { this.productid, this.categoryid, this.name, this.logo, this.descn });
    }

    public Category getCategory() {
        return category;
    }

    public Product setCategory(Category category) {
        this.category = category;
        this.categoryid.set(category.catid.get());
        return this;
    }

    public Product addItem(Item m) {
        m.setProduct(this);
        super.addModel(m);
        return this;
    }

    public Product addItem(Item... mArray) {
        for (Item m : mArray)
            addItem(m);
        return this;
    }

    public List<Item> getItemList() {
        return super.getModelList(Item.class);
    }

    @Override
    protected Product newInstance(ModelTable t, short modelType) {
        return new Product(t, modelType);
    }

    @Override
    protected List<Model<?>> newAssociateInstances() {
        ArrayList<Model<?>> list = new ArrayList<>();
        Item m1 = new Item();
        addItem(m1);
        list.add(m1);
        return list;
    }

    public static Product decode(String str) {
        return new Product().decode0(str);
    }
}
