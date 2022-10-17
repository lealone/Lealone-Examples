package org.lealone.examples.petstore.dal.model;

import org.lealone.plugins.orm.Model;
import org.lealone.plugins.orm.ModelProperty;
import org.lealone.plugins.orm.ModelTable;
import org.lealone.plugins.orm.property.PString;

/**
 * Model for table 'USER'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
public class User extends Model<User> {

    public static final User dao = new User(null, ROOT_DAO);

    public final PString<User> userId;
    public final PString<User> password;
    public final PString<User> roles;

    public User() {
        this(null, REGULAR_MODEL);
    }

    private User(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "USER") : t, modelType);
        userId = new PString<>("USER_ID", this);
        password = new PString<>("PASSWORD", this);
        roles = new PString<>("ROLES", this);
        super.setModelProperties(new ModelProperty[] { userId, password, roles });
    }

    @Override
    protected User newInstance(ModelTable t, short modelType) {
        return new User(t, modelType);
    }

    public static User decode(String str) {
        return new User().decode0(str);
    }
}
