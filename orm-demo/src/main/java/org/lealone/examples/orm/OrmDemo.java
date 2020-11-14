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
package org.lealone.examples.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.lealone.db.Constants;
import org.lealone.db.SysProperties;
import org.lealone.transaction.TransactionEngine;
import org.lealone.transaction.TransactionEngineManager;
import org.lealone.transaction.aote.log.LogSyncService;

public class OrmDemo {

    // 通过JDBC访问的数据库的URL
    static String jdbcUrl = "jdbc:lealone:embed:test";

    public static void main(String[] args) throws Exception {
        initTransactionEngine();

        createTable();

        CrudTest.run();
    }

    public static void createTable() throws Exception {
        System.setProperty("lealone.jdbc.url", jdbcUrl);

        execute("drop table if exists `order`");
        execute("drop table if exists customer");

        System.out.println("create table: customer");

        // 创建表: customer，会生成一个名为Customer的模型类
        String sql = "create table if not exists customer(id long primary key, name char(10), notes varchar, phone int)" //
                + "   package 'org.lealone.examples.orm.generated'" // Customer类所在的包名
                + "   generate code './src/main/java'"; // Customer类的源文件所在的根目录
        execute(sql);

        System.out.println("create table: order");

        // order是关键字，所以要用特殊方式表式
        // 创建表: user，会生成一个名为User的模型类
        sql = "create table if not exists `order`(customer_id long, order_id int primary key, order_date date, total double,"
                + "   FOREIGN KEY(customer_id) REFERENCES customer(id))" //
                + "   package 'org.lealone.examples.orm.generated'" // User类所在的包名
                + "   generate code './src/main/java'"; // User类的源文件所在的根目录
        execute(sql);
    }

    public static void execute(String sql) throws Exception {
        Connection conn = DriverManager.getConnection(jdbcUrl);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }

    public static void initTransactionEngine() {
        SysProperties.setBaseDir("./target/db_base_dir");
        TransactionEngine te = TransactionEngineManager.getInstance()
                .getEngine(Constants.DEFAULT_TRANSACTION_ENGINE_NAME);

        Map<String, String> config = new HashMap<>();
        config.put("base_dir", "./target/db_base_dir");
        config.put("redo_log_dir", "redo_log");
        config.put("log_sync_type", LogSyncService.LOG_SYNC_TYPE_PERIODIC);
        te.init(config);
    }
}
