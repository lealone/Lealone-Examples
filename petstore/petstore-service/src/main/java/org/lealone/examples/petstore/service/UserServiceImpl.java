package org.lealone.examples.petstore.service;

import org.lealone.examples.petstore.dal.model.Account;
import org.lealone.examples.petstore.dal.model.User;
import org.lealone.examples.petstore.service.generated.UserService;
import org.lealone.orm.json.JsonObject;

public class UserServiceImpl implements UserService {

    @Override
    public User login(String userId, String password) {
        User user = User.dao.where().userId.eq(userId).and().password.eq(password).findOne();
        if (user == null)
            throw new RuntimeException("用户名不存在或密码错误");
        return user;
    }

    @Override
    public void register(User user) {
        user.insert();
    }

    @Override
    public void update(User user) {
        // User old = User.dao.where().userId.eq(user.userId).findOne();
        user.update();
    }

    @Override
    public String getUser(String userId) {
        User user = User.dao.where().userId.eq(userId).findOne();
        Account account = Account.dao.where().userId.eq(userId).findOne();

        JsonObject json = new JsonObject();
        json.put("user", user);
        json.put("account", account);
        return json.encode();
    }

}
