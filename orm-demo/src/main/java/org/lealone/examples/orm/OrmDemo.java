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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.lealone.examples.orm.generated.Customer;
import org.lealone.examples.orm.generated.Order;

public class OrmDemo {

    public static void main(String[] args) throws Exception {
        createTable();
        testCrud();
        testJoin();
        testTransaction();
    }

    // ------------ 以下代码中出现的的 where() 都是可选的 ------------

    // 单表crud操作
    static void testCrud() {
        // insert
        new Customer().id.set(1001).name.set("rob").phone.set(12345678).insert();

        Customer dao = Customer.dao;

        // find one
        Customer c = dao.where().id.eq(1001).findOne();
        assertEquals("rob", c.name.get());

        // find list
        List<Customer> list = dao.id.eq(1001).findList();
        assertEquals(1, list.size());

        // count
        int count = dao.findCount();
        assertEquals(1, count);

        // 单记录 update
        c.notes.set("test").update();
        c = dao.where().id.eq(1001).findOne();
        assertEquals("test", c.notes.get());

        // 批量 update
        dao.notes.set("batch update").where().name.startsWith("rob").update();
        c = dao.id.eq(1001).findOne();
        assertEquals("batch update", c.notes.get());

        // 分页查询
        list = dao.offset(0).limit(1).findList();
        assertEquals(1, list.size());

        // delete
        dao.where().id.eq(1001).delete();
        count = dao.findCount();
        assertEquals(0, count);
    }

    // 两表关联查询
    static void testJoin() {
        // 批量增加有关联的记录
        Order o1 = new Order().orderId.set(2001).orderDate.set("2018-01-01");
        Order o2 = new Order().orderId.set(2002).orderDate.set("2018-01-01");
        Customer customer = new Customer().id.set(1002).name.set("customer1");
        customer.addOrder(o1, o2).insert();
        // 调用addOrder后，Order的customerId字段会自动对应Customer的id字段
        assertEquals(o1.customerId.get(), customer.id.get());

        // 关联查询
        Customer c = Customer.dao;
        Order o = Order.dao;
        customer = c.join(o).on().id.eq(o.customerId).where().id.eq(1002).findOne();

        // 一个customer对应两个Order
        List<Order> orderList = customer.getOrderList();
        assertEquals(2, orderList.size());
        assertTrue(customer == orderList.get(0).getCustomer());
    }

    // 测试事务
    static void testTransaction() {
        try {
            Customer.dao.beginTransaction(); // 开始一个新事务

            new Customer().id.set(1003).name.set("rob3").insert();
            new Customer().id.set(1004).name.set("rob4").insert();

            Customer.dao.commitTransaction(); // 提交事务
        } catch (Exception e) {
            Customer.dao.rollbackTransaction(); // 回滚事务
            e.printStackTrace();
        }

        int count = Customer.dao.where().id.eq(1003).or().id.eq(1004).findCount();
        assertEquals(2, count);
    }

    // 执行建表脚本，同时自动生成对应的模型类的代码
    static void createTable() throws Exception {
        String jdbcUrl = "jdbc:lealone:embed:test";
        System.setProperty("lealone.jdbc.url", jdbcUrl);

        try (Connection conn = DriverManager.getConnection(jdbcUrl); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("RUNSCRIPT FROM './src/main/resources/tables.sql'");
        }
    }
}
