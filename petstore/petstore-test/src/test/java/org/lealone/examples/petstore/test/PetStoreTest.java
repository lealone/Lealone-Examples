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
package org.lealone.examples.petstore.test;

import org.lealone.common.exceptions.ConfigException;
import org.lealone.examples.petstore.main.PetStore;
import org.lealone.p2p.config.Config;
import org.lealone.p2p.config.Config.PluggableEngineDef;
import org.lealone.p2p.config.YamlConfigLoader;
import org.lealone.server.http.HttpServerEngine;

public class PetStoreTest extends YamlConfigLoader {

    public static void main(String[] args) {
        System.setProperty("lealone.config.loader", PetStoreTest.class.getName());
        PetStore.main(args);
    }

    @Override
    public void applyConfig(Config config) throws ConfigException {
        // 动态生成绝对路径的webRoot，使用相对路径在eclipse和idea下面总有一个不正确
        String webRoot = PetStore.getAbsolutePath("petstore-web/web");
        for (PluggableEngineDef e : config.protocol_server_engines) {
            if (HttpServerEngine.NAME.equalsIgnoreCase(e.name)) {
                e.parameters.put("web_root", webRoot);
            }
        }
        super.applyConfig(config);
    }
}
