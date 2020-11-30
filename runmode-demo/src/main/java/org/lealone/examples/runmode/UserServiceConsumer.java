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
package org.lealone.examples.runmode;

import org.lealone.examples.runmode.generated.model.User;
import org.lealone.examples.runmode.generated.service.UserService;

public class UserServiceConsumer {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:lealone:tcp://localhost/test?user=root&password=";
        UserService s = UserService.create(jdbcUrl);

        String name = "zhh";

        Long rowId = s.addUser(name, 18);

        System.out.println("add user: " + name + ", return id: " + rowId);

        User user = s.findByName(name);

        System.out.println("user: " + user);
    }
}
