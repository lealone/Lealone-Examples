package org.lealone.examples.petstore.dal.model;

import java.util.ArrayList;
import java.util.List;
import org.lealone.orm.Model;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PString;

/**
 * Model for table 'CATEGORY'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Category extends Model<Category> {

    public static final Category dao = new Category(null, ROOT_DAO);

    public final PString<Category> catid;
    public final PString<Category> name;
    public final PString<Category> logo;
    public final PString<Category> descn;

    public Category() {
        this(null, REGULAR_MODEL);
    }

    private Category(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "CATEGORY") : t, modelType);
        super.setRoot(this);

        this.catid = new PString<>("CATID", this);
        this.name = new PString<>("NAME", this);
        this.logo = new PString<>("LOGO", this);
        this.descn = new PString<>("DESCN", this);
        super.setModelProperties(new ModelProperty[] { this.catid, this.name, this.logo, this.descn });
    }

    public Category addProduct(Product m) {
        m.setCategory(this);
        super.addModel(m);
        return this;
    }

    public Category addProduct(Product... mArray) {
        for (Product m : mArray)
            addProduct(m);
        return this;
    }

    public List<Product> getProductList() {
        return super.getModelList(Product.class);
    }

    @Override
    protected Category newInstance(ModelTable t, short modelType) {
        return new Category(t, modelType);
    }

    @Override
    protected List<Model<?>> newAssociateInstances() {
        ArrayList<Model<?>> list = new ArrayList<>();
        Product m1 = new Product();
        addProduct(m1);
        list.add(m1);
        return list;
    }

    public static Category decode(String str) {
        return new Category().decode0(str);
    }
}
