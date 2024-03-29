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
package com.lealone.examples.python;

import com.lealone.main.Lealone;

// 在前端调用 hello_service 服务，用以下 url:
// http://localhost:9000/service/hello_service/hello?name=zhh

// 在前端调用 user_service 服务，请在浏览器中打开下面这个 url 进行测试:
// http://localhost:9000/fullStack.html
public class PythonDemo {

    public static void main(String[] args) {
        Lealone.main(args, () -> runScript());
    }

    public static void runScript() {
        String url = "jdbc:lealone:tcp://localhost:9210/lealone?user=root";
        // 执行建表脚本，同时自动生成对应的模型类的代码
        // 执行服务创建脚本，同时自动生成对应的服务接口代码
        Lealone.runScript(url, "./sql/tables.sql", "./sql/services.sql");
    }
}
