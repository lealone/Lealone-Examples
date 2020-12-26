package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Sequence.SequenceDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'SEQUENCE'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = SequenceDeserializer.class)
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

    static class SequenceDeserializer extends ModelDeserializer<Sequence> {
        @Override
        protected Model<Sequence> newModelInstance() {
            return new Sequence();
        }
    }
}
