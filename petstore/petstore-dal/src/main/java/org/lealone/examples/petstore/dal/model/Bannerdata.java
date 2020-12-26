package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Bannerdata.BannerdataDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PString;

/**
 * Model for table 'BANNERDATA'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = BannerdataDeserializer.class)
public class Bannerdata extends Model<Bannerdata> {

    public static final Bannerdata dao = new Bannerdata(null, ROOT_DAO);

    public final PString<Bannerdata> favcategory;
    public final PString<Bannerdata> bannername;

    public Bannerdata() {
        this(null, REGULAR_MODEL);
    }

    private Bannerdata(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "BANNERDATA") : t, modelType);
        super.setRoot(this);

        this.favcategory = new PString<>("FAVCATEGORY", this);
        this.bannername = new PString<>("BANNERNAME", this);
        super.setModelProperties(new ModelProperty[] { this.favcategory, this.bannername });
    }

    @Override
    protected Bannerdata newInstance(ModelTable t, short modelType) {
        return new Bannerdata(t, modelType);
    }

    static class BannerdataDeserializer extends ModelDeserializer<Bannerdata> {
        @Override
        protected Model<Bannerdata> newModelInstance() {
            return new Bannerdata();
        }
    }
}
