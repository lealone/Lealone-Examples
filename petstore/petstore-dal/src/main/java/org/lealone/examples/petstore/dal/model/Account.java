package org.lealone.examples.petstore.dal.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lealone.examples.petstore.dal.model.Account.AccountDeserializer;
import org.lealone.orm.Model;
import org.lealone.orm.ModelDeserializer;
import org.lealone.orm.ModelProperty;
import org.lealone.orm.ModelSerializer;
import org.lealone.orm.ModelTable;
import org.lealone.orm.property.PDate;
import org.lealone.orm.property.PString;

/**
 * Model for table 'ACCOUNT'.
 *
 * THIS IS A GENERATED OBJECT, DO NOT MODIFY THIS CLASS.
 */
@JsonSerialize(using = ModelSerializer.class)
@JsonDeserialize(using = AccountDeserializer.class)
public class Account extends Model<Account> {

    public static final Account dao = new Account(null, ROOT_DAO);

    public final PString<Account> userId;
    public final PString<Account> email;
    public final PString<Account> firstName;
    public final PString<Account> lastName;
    public final PString<Account> status;
    public final PString<Account> address1;
    public final PString<Account> address2;
    public final PString<Account> city;
    public final PString<Account> state;
    public final PString<Account> zip;
    public final PString<Account> country;
    public final PString<Account> phone;
    public final PString<Account> creditCardNumber;
    public final PString<Account> creditCardType;
    public final PDate<Account> creditCardExpiry;

    public Account() {
        this(null, REGULAR_MODEL);
    }

    private Account(ModelTable t, short modelType) {
        super(t == null ? new ModelTable("PETSTORE", "PUBLIC", "ACCOUNT") : t, modelType);
        super.setRoot(this);

        this.userId = new PString<>("USER_ID", this);
        this.email = new PString<>("EMAIL", this);
        this.firstName = new PString<>("FIRST_NAME", this);
        this.lastName = new PString<>("LAST_NAME", this);
        this.status = new PString<>("STATUS", this);
        this.address1 = new PString<>("ADDRESS_1", this);
        this.address2 = new PString<>("ADDRESS_2", this);
        this.city = new PString<>("CITY", this);
        this.state = new PString<>("STATE", this);
        this.zip = new PString<>("ZIP", this);
        this.country = new PString<>("COUNTRY", this);
        this.phone = new PString<>("PHONE", this);
        this.creditCardNumber = new PString<>("CREDIT_CARD_NUMBER", this);
        this.creditCardType = new PString<>("CREDIT_CARD_TYPE", this);
        this.creditCardExpiry = new PDate<>("CREDIT_CARD_EXPIRY", this);
        super.setModelProperties(new ModelProperty[] { this.userId, this.email, this.firstName, this.lastName, this.status, this.address1, this.address2, this.city, this.state, this.zip, this.country, this.phone, this.creditCardNumber, this.creditCardType, this.creditCardExpiry });
    }

    @Override
    protected Account newInstance(ModelTable t, short modelType) {
        return new Account(t, modelType);
    }

    static class AccountDeserializer extends ModelDeserializer<Account> {
        @Override
        protected Model<Account> newModelInstance() {
            return new Account();
        }
    }
}
