package org.lealone.examples.petstore.dal.model;

import org.lealone.orm.Model;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'SEQUENCE'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Sequence extends Model<Sequence> {

    public static final Sequence dao = new Sequence(null, ROOT_DAO);

    public final PString<Sequence> name;
    public final PInteger<Sequence> nextid;

    public Sequence() {
        this(null, REGULAR_MODEL);
    }

    private Sequence(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "SEQUENCE") : t, modelType);
        super.setRoot(this);

        this.name = new PString<>("NAME", this);
        this.nextid = new PInteger<>("NEXTID", this);
        super.setModelProperties(new ModelProperty[] { this.name, this.nextid });
    }

    @Override
    protected Sequence newInstance(ModelTable t, short modelType) {
        return new Sequence(t, modelType);
    }

    public static Sequence decode(String str) {
        return new Sequence().decode0(str);
    }
}
