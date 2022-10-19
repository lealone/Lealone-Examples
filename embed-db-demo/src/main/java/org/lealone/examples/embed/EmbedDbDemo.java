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
package org.lealone.examples.embed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;

public class EmbedDbDemo {

    public static void main(String[] args) throws Exception {
        // 访问lealone数据库
        crud("jdbc:lealone:embed:lealone");

        // 自动创建mydb1数据库
        crud("jdbc:lealone:embed:mydb1");

        // 自动创建mydb2数据库，但是不持久化
        crud("jdbc:lealone:embed:mydb2?persistent=false");
    }

    // 通过标准JDBC接口进行CRUD操作
    public static void crud(String url) throws Exception {
        Connection conn = DriverManager.getConnection(url, "root", "");
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS test");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS test (f1 int primary key, f2 long)");
        stmt.executeUpdate("INSERT INTO test(f1, f2) VALUES(1, 1)");
        stmt.executeUpdate("UPDATE test SET f2 = 2 WHERE f1 = 1");
        ResultSet rs = stmt.executeQuery("SELECT * FROM test");
        Assert.assertTrue(rs.next());
        System.out.println("f1=" + rs.getInt(1) + " f2=" + rs.getLong(2));
        Assert.assertFalse(rs.next());
        rs.close();
        stmt.executeUpdate("DELETE FROM test WHERE f1 = 1");
        rs = stmt.executeQuery("SELECT * FROM test");
        Assert.assertFalse(rs.next());
        rs.close();
        stmt.close();
        conn.close();
    }
}
