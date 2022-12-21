package org.lealone.examples.petstore.dal.model;

import org.lealone.plugins.orm.Model;
import org.lealone.plugins.orm.ModelProperty;
import org.lealone.plugins.orm.ModelTable;
import org.lealone.plugins.orm.format.JsonFormat;
import org.lealone.plugins.orm.property.PString;

/**
 * Model for table 'BANNER_DATA'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class BannerData extends Model<BannerData> {

    public static final BannerData dao = new BannerData(null, ROOT_DAO);

    public final PString<BannerData> favcategory;
    public final PString<BannerData> bannerName;

    public BannerData() {
        this(null, REGULAR_MODEL);
    }

    private BannerData(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "BANNER_DATA") : t, modelType);
        favcategory = new PString<>("FAVCATEGORY", this);
        bannerName = new PString<>("BANNER_NAME", this);
        super.setModelProperties(new ModelProperty[] { favcategory, bannerName });
    }

    @Override
    protected BannerData newInstance(ModelTable t, short modelType) {
        return new BannerData(t, modelType);
    }

    public static BannerData decode(String str) {
        return decode(str, null);
    }

    public static BannerData decode(String str, JsonFormat format) {
        return new BannerData().decode0(str, format);
    }
}
