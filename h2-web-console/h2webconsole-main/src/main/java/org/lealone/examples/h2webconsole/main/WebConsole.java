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
package org.lealone.examples.h2webconsole.main;

import java.sql.SQLException;
import java.util.ArrayList;

import org.lealone.main.Lealone;

public class WebConsole {
    public static void main(String[] args) {
        new Thread(() -> {
            // System.setProperty("DATABASE_TO_UPPER", "false");
            System.setProperty("h2.lobInDatabase", "false");
            System.setProperty("h2.lobClientMaxSizeMemory", "1024");
            System.setProperty("java.io.tmpdir", "./target/mytest/tmp");
            System.setProperty("h2.baseDir", "./target/mytest");
            // System.setProperty("h2.check2", "true");
            ArrayList<String> list = new ArrayList<String>();
            // list.add("-tcp");
            // //list.add("-tool");
            // org.h2.tools.Server.main(list.toArray(new String[list.size()]));
            //
            // list.add("-tcp");
            // list.add("-tcpPort");
            // list.add("9092");

            // 测试org.h2.server.TcpServer.checkKeyAndGetDatabaseName(String)
            // list.add("-key");
            // list.add("mydb");
            // list.add("mydatabase");

            // list.add("-pg");
            list.add("-tcp");
            list.add("-web");
            // list.add("-ifExists");
            list.add("-ifNotExists");
            list.add("-tcpAllowOthers");
            list.add("-webAdminPassword");
            list.add("root");
            try {
                org.h2.tools.Server.main(list.toArray(new String[list.size()]));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

        // 请在浏览器中打开下面这个URL进行测试:
        // http://localhost:9000/
        Lealone.main(args);
    }
}
