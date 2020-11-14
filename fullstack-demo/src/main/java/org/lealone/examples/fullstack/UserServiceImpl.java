/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lealone.examples.fullstack;

import org.lealone.examples.fullstack.generated.model.User;
import org.lealone.examples.fullstack.generated.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public Long addUser(String name, Integer age) {
        // 如果 name = 'zhh', age = 18
        // 对应的sql是: insert into user(name, age) values('zhh', 18);
        User user = new User();
        user.name.set(name).age.set(age).insert(); // 链式调用
        return user.id.get();
    }

    @Override
    public User findByName(String name) {
        // 如果 name = 'zhh'
        // 对应的sql是: select * from user where name = 'zhh' limit 1
        return User.dao.where().name.eq(name).findOne();
    }
}
