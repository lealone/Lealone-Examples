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
package com.lealone.examples.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloService {

    // 用这样的url打开: http://localhost:8080/service/hello_service/hello?name=zhh
    public String hello(String name) {
        return "Hello " + HelloService.getUser(name);
    }

    public static void create() {
        try {
            Connection conn = getConnection();
            String sql = "create service if not exists hello_service (hello(name varchar) varchar)" //
                    + " implement by '" + HelloService.class.getName() + "'";
            executeUpdate(conn, sql);
            executeUpdate(conn, "drop table if exists user");
            executeUpdate(conn, "create table if not exists user(name varchar primary key, age int)");
            executeUpdate(conn, "insert into user values('zhh', 18)");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getUser(String name) {
        String userInfo = null;
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("select name, age from user where name=?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userInfo = "User(name: " + rs.getString(1) + ", age: " + rs.getInt(2) + ")";
            } else {
                userInfo = "User( " + name + " not found)";
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            userInfo = e.getMessage();
        }
        return userInfo;
    }

    public static void executeUpdate(Connection conn, String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:lealone:embed:lealone", "root", "");
    }
}
