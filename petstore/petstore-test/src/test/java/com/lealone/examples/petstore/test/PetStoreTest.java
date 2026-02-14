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
package com.lealone.examples.petstore.test;

import java.util.ArrayList;

import com.lealone.common.exceptions.ConfigException;
import com.lealone.examples.petstore.main.PetStore;
import com.lealone.plugins.service.http.HttpServerEngine;
import com.lealone.sql.config.Config;
import com.lealone.sql.config.ConfigListener;

public class PetStoreTest implements ConfigListener {

    public static void main(String[] args) {
        System.setProperty("lealone.config.listener ", PetStoreTest.class.getName());
        ArrayList<String> list = new ArrayList<>();
        list.add("-database");
        list.add("petstore");

        list.add("-initSql");
        list.add("set @srcPath '" + PetStore.getAbsolutePath("petstore-dal/src/main/java") + "'");

        list.add("-sqlScripts");
        // 执行建表脚本，同时自动生成对应的模型类的代码
        String sqlScripts = PetStore.getAbsolutePath("petstore-dal/src/main/resources/tables.sql");
        // 初始化数据
        sqlScripts += "," + PetStore.getAbsolutePath("petstore-dal/src/main/resources/init-data.sql");
        // 执行服务创建脚本，同时自动生成对应的服务接口代码
        sqlScripts += "," + PetStore.getAbsolutePath("petstore-service/src/main/resources/services.sql");
        list.add(sqlScripts);

        PetStore.main(list.toArray(args));
    }

    @Override
    public void applyConfig(Config config) throws ConfigException {
        // 动态生成绝对路径的webRoot，使用相对路径在eclipse和idea下面总有一个不正确
        String webRoot = PetStore.getAbsolutePath("petstore-web/web");
        config.getProtocolServerParameters(HttpServerEngine.NAME).put("web_root", webRoot);
    }
}
