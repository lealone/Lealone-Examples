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
package com.lealone.examples.rpc;

import com.lealone.plugins.boot.LealoneApplication;

// 请在浏览器中打开下面的 URL 进行测试:
// http://localhost:8080/service/hello_service/say_hello?name=zhh
// http://localhost:8080/hello.html
public class RpcDemo {

    public static void main(String[] args) {
        // 1. 使用默认参数启动
        LealoneApplication.start("mydb", "./sql/services.sql");

        // 2. 按定制的参数启动
        // LealoneApplication app = new LealoneApplication();
        // app.setBaseDir("./target/test-data");
        // app.setWebRoot("./web");
        // app.setDatabase("mydb");
        // app.setSqlScripts("./sql/services.sql");
        // app.start();
    }
}
