package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.User.UserDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PString;

/**
 * Model for table 'USER'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = UserDeserializer.class)
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
        super.setRoot(this);

        this.userId = new PString<>("USER_ID", this);
        this.password = new PString<>("PASSWORD", this);
        this.roles = new PString<>("ROLES", this);
        super.setModelProperties(new ModelProperty[] { this.userId, this.password, this.roles });
    }

    @Override
    protected User newInstance(ModelTable t, short modelType) {
        return new User(t, modelType);
    }

    static class UserDeserializer extends ModelDeserializer<User> {
        @Override
        protected Model<User> newModelInstance() {
            return new User();
        }
    }
}
