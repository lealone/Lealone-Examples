package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.BannerData.BannerDataDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PString;

/**
 * Model for table 'BANNER_DATA'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = BannerDataDeserializer.class)
public class BannerData extends Model<BannerData> {

    public static final BannerData dao = new BannerData(null, ROOT_DAO);

    public final PString<BannerData> favcategory;
    public final PString<BannerData> bannerName;

    public BannerData() {
        this(null, REGULAR_MODEL);
    }

    private BannerData(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "BANNER_DATA") : t, modelType);
        super.setRoot(this);

        this.favcategory = new PString<>("FAVCATEGORY", this);
        this.bannerName = new PString<>("BANNER_NAME", this);
        super.setModelProperties(new ModelProperty[] { this.favcategory, this.bannerName });
    }

    @Override
    protected BannerData newInstance(ModelTable t, short modelType) {
        return new BannerData(t, modelType);
    }

    static class BannerDataDeserializer extends ModelDeserializer<BannerData> {
        @Override
        protected Model<BannerData> newModelInstance() {
            return new BannerData();
        }
    }
}
