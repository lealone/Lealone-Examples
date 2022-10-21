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
package org.lealone.examples.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.lealone.plugins.spring",
        "org.lealone.examples.spring" })
public class SpringDemo {
    // 用 mvn package spring-boot:repackage 打出的 jar 包在运行时调用 java compiler api 编译源代码会报错
    // 只能用 eclipse 导出一个可执行的 jar 包
    // https://user-images.githubusercontent.com/872655/197129734-a1fc9612-c603-4773-8c30-c08d8d061dc4.png
    public static void main(String[] args) {
        HelloService.create();
        SpringApplication.run(SpringDemo.class, args);
    }
}
