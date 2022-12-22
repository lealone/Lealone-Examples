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
package org.lealone.examples.polyglot;

import org.lealone.main.Lealone;

// 请在浏览器中打开下面的 URL 进行测试:
// http://localhost:9000/service/hello_service/hello?name=zhh
// http://localhost:9000/service/time_service/get_current_time
public class PolyglotDemo {

    public static void main(String[] args) {
        Lealone.main(args, () -> runScript());
    }

    public static void runScript() {
        String url = "jdbc:lealone:tcp://localhost:9210/lealone?user=root";
        // 执行服务创建脚本，同时自动生成对应的服务接口代码
        Lealone.runScript(url, "./sql/services.sql");
    }
}
