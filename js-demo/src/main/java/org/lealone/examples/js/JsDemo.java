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
package org.lealone.examples.js;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.lealone.main.Lealone;

//在前端调用 hello_service 服务，用以下 url:
//http://localhost:9000/service/hello_service/hello?name=zhh

//在前端调用 user_service 服务，请在浏览器中打开下面这个 url 进行测试:
//http://localhost:9000/fullStack.html
public class JsDemo {

    public static void main(String[] args) {
        Lealone.run(args, () -> runScript());
    }

    // 执行 tables.sql 和 services.sql 脚本，创建表和服务
    public static void runScript() {
        String url = "jdbc:lealone:tcp://localhost:9210/lealone?user=root";
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("RUNSCRIPT FROM './sql/tables.sql'");
            stmt.executeUpdate("RUNSCRIPT FROM './sql/services.sql'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
