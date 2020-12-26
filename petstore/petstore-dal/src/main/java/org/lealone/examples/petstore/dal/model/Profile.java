package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Profile.ProfileDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PInteger;
import org.lealone.orm.property.PString;

/**
 * Model for table 'PROFILE'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = ProfileDeserializer.class)
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
        super.setRoot(this);

        this.userId = new PString<>("USER_ID", this);
        this.languagePreference = new PString<>("LANGUAGE_PREFERENCE", this);
        this.favoriteCategoryId = new PString<>("FAVORITE_CATEGORY_ID", this);
        this.listOption = new PInteger<>("LIST_OPTION", this);
        this.bannerOpttion = new PInteger<>("BANNER_OPTTION", this);
        super.setModelProperties(new ModelProperty[] { this.userId, this.languagePreference, this.favoriteCategoryId, this.listOption, this.bannerOpttion });
    }

    @Override
    protected Profile newInstance(ModelTable t, short modelType) {
        return new Profile(t, modelType);
    }

    static class ProfileDeserializer extends ModelDeserializer<Profile> {
        @Override
        protected Model<Profile> newModelInstance() {
            return new Profile();
        }
    }
}
