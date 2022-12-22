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
package org.lealone.examples.rpc;

import org.lealone.main.Lealone;
import org.lealone.plugins.service.http.HttpServer;

public class RpcDemo {

    // 通过 JDBC 访问的数据库的 URL
    static String jdbcUrl = "jdbc:lealone:embed:test";

    public static void main(String[] args) {
        // 静态资源文件的根目录，如果有多个可以用逗号分隔
        String webRoot = args.length == 1 ? args[0] : "./web";

        // 启动 HttpServer，请在浏览器中打开下面的 URL 进行测试:
        // http://localhost:8080/hello.html
        HttpServer server = HttpServer.create();
        server.setJdbcUrl(jdbcUrl);
        server.setWebRoot(webRoot);
        server.start();

        createService();
    }

    // 执行 services.sql 脚本，创建服务
    public static void createService() {
        Lealone.runScript(jdbcUrl, "./src/main/resources/services.sql");
    }
}
