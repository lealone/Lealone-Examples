package com.lealone.examples.js;

import com.lealone.plugins.orm.Model;
import com.lealone.plugins.orm.ModelProperty;
import com.lealone.plugins.orm.ModelTable;
import com.lealone.plugins.orm.format.JsonFormat;
import com.lealone.plugins.orm.property.PInteger;
import com.lealone.plugins.orm.property.PLong;
import com.lealone.plugins.orm.property.PString;

/**
 * Model for table 'USER'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class User extends Model<User> {

    public static final User dao = new User(null, ROOT_DAO);

    public final PLong<User> id;
    public final PString<User> name;
    public final PInteger<User> age;

    public User() {
        this(null, REGULAR_MODEL);
    }

    private User(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("LEALONE", "PUBLIC", "USER") : t, modelType);
        id = new PLong<>("ID", this);
        name = new PString<>("NAME", this);
        age = new PInteger<>("AGE", this);
        super.setModelProperties(new ModelProperty[] { id, name, age });
    }

    @Override
    protected User newInstance(ModelTable t, short modelType) {
        return new User(t, modelType);
    }

    public static User decode(String str) {
        return decode(str, null);
    }

    public static User decode(String str, JsonFormat format) {
        return new User().decode0(str, format);
    }
}
