package com.lealone.examples.petstore.dal.model;

import com.lealone.plugins.orm.Model;
import com.lealone.plugins.orm.ModelProperty;
import com.lealone.plugins.orm.ModelTable;
import com.lealone.plugins.orm.format.JsonFormat;
import com.lealone.plugins.orm.property.PInteger;
import com.lealone.plugins.orm.property.PString;

/**
 * Model for table 'PROFILE'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class Profile extends Model<Profile> {

    public static final Profile dao = new Profile(null, ROOT_DAO);

    public final PString<Profile> userId;
    public final PString<Profile> languagePreference;
    public final PString<Profile> favoriteCategoryId;
    public final PInteger<Profile> listOption;
    public final PInteger<Profile> bannerOpttion;

    public Profile() {
        this(null, REGULAR_MODEL);
    }

    private Profile(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "PROFILE") : t, modelType);
        userId = new PString<>("USER_ID", this);
        languagePreference = new PString<>("LANGUAGE_PREFERENCE", this);
        favoriteCategoryId = new PString<>("FAVORITE_CATEGORY_ID", this);
        listOption = new PInteger<>("LIST_OPTION", this);
        bannerOpttion = new PInteger<>("BANNER_OPTTION", this);
        super.setModelProperties(new ModelProperty[] { userId, languagePreference, favoriteCategoryId, listOption, bannerOpttion });
    }

    @Override
    protected Profile newInstance(ModelTable t, short modelType) {
        return new Profile(t, modelType);
    }

    public static Profile decode(String str) {
        return decode(str, null);
    }

    public static Profile decode(String str, JsonFormat format) {
        return new Profile().decode0(str, format);
    }
}
