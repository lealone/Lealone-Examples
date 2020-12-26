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
package org.lealone.examples.petstore.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.lealone.db.SysProperties;
import org.lealone.main.Lealone;
import org.lealone.server.http.HttpServer;

public class PetStoreDemo {

    public static void main(String[] args) {
        // 请在浏览器中打开下面这个URL进行测试:
        // http://localhost:9000/index.html
        Lealone.main(args);
    }

    // 通过JDBC访问的数据库的URL
    static String jdbcUrl = "jdbc:lealone:embed:petstore";

    public static void main2(String[] args) throws Exception {
        String baseDir = "../petstore-dal/target/db_base_dir";
        SysProperties.setBaseDir(baseDir);

        // 静态资源文件的根目录，如果有多个可以用逗号分隔
        String webRoot = args.length == 1 ? args[0] : "./web";

        // 启动HttpServer，请在浏览器中打开下面这个URL进行测试:
        // http://localhost:8080/hello.html
        HttpServer server = new HttpServer();
        server.setJdbcUrl(jdbcUrl);
        server.setWebRoot(webRoot);
        server.setPort(9000);
        server.start();

        // createService();
    }

    // 执行服务创建脚本，同时自动生成对应的服务接口代码
    public static void createService() throws Exception {
        System.setProperty("lealone.jdbc.url", jdbcUrl);
        try (Connection conn = DriverManager.getConnection(jdbcUrl); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("RUNSCRIPT FROM './src/main/resources/services.sql'");
        }
    }
}
