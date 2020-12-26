package org.lealone.examples.petstore.service;

import org.lealone.examples.petstore.dal.model.User;
import org.lealone.examples.petstore.service.generated.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public User login(String userId, String password) {
        return User.dao.where().userId.eq(userId).and().password.eq(password).findOne();
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
    public User getUser(String userId) {
        return User.dao.where().userId.eq(userId).findOne();
    }

}
