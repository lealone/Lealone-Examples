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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.lealone.plugins.vertx.server.HttpServer;

public class FullStackDemo {

    // 通过JDBC访问的数据库的URL
    static String jdbcUrl = "jdbc:lealone:embed:test";

    public static void main(String[] args) throws Exception {
        // 静态资源文件的根目录，如果有多个可以用逗号分隔
        String webRoot = args.length == 1 ? args[0] : "./web";

        // 启动HttpServer，请在浏览器中打开下面这个URL进行测试:
        // http://localhost:8080/fullStack.html
        HttpServer server = new HttpServer();
        server.setJdbcUrl(jdbcUrl);
        server.setWebRoot(webRoot);
        server.start();

        createTable();
        createService();
    }

    public static void createTable() throws Exception {
        System.setProperty("lealone.jdbc.url", jdbcUrl);

        // execute("drop table if exists user");
        // 创建表: user，会生成一个名为User的模型类
        String sql = "create table if not exists user(id long auto_increment primary key, name varchar, age int)" //
                + "   package 'org.lealone.examples.fullstack.generated.model'" // User类所在的包名
                + "   generate code './src/main/java'"; // User类的源文件所在的根目录

        execute(sql);
    }

    public static void createService() throws Exception {
        execute("drop service if exists user_service");

        // 创建服务: user_service，会生成一个对应的UserService接口
        String sql = "create service if not exists user_service (" //
                + "     add_user(name varchar, age int) long," // 定义UserService接口方法 add_user
                + "     find_by_name(name varchar) user" // 定义UserService接口方法find_by_name
                + "   )" //
                + "   package 'org.lealone.examples.fullstack.generated.service'" // UserService接口所在的包名
                + "   implement by 'org.lealone.examples.fullstack.UserServiceImpl'" // UserService接口的默认实现类
                + "   generate code './src/main/java'"; // UserService接口源文件的根目录

        execute(sql);
    }

    public static void execute(String sql) throws Exception {
        Connection conn = DriverManager.getConnection(jdbcUrl);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }
}
