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
package com.lealone.examples.storage;

import com.lealone.db.PluginManager;
import com.lealone.storage.Storage;
import com.lealone.storage.StorageBuilder;
import com.lealone.storage.StorageEngine;
import com.lealone.storage.StorageMap;

public class StorageEngineDemo {

    public static void main(String[] args) {
        Storage storage = getStorage("aose");
        testMap(storage);
        testAsyncMap(storage);

        storage = getStorage("memory");
        testMap(storage);
        testAsyncMap(storage);
        System.exit(0); // 关闭所有的非守护线程
    }

    private static void testMap(Storage storage) {
        StorageMap<String, Integer> map = storage.openMap("test", null);
        map.put("a", 100);
        map.put("b", 200);

        Integer v = map.get("a");
        System.out.println(v);

        map.cursor().forEachRemaining(k -> {
            System.out.println(map.get(k));
        });

        map.save();
    }

    private static void testAsyncMap(Storage storage) {
        StorageMap<String, Integer> map = storage.openMap("test", null);
        map.put("c", 300, ac -> {
            System.out.println("Async old value: " + ac.getResult());
        });
        map.put("d", 400, ac -> {
            System.out.println("Async old value: " + ac.getResult());
        });
        Integer v = map.get("c");
        System.out.println(v);

        map.cursor().forEachRemaining(k -> {
            System.out.println(map.get(k));
        });

        map.save();
    }

    private static Storage getStorage(String name) {
        StorageEngine se = PluginManager.getPlugin(StorageEngine.class, name);
        StorageBuilder builder = se.getStorageBuilder();
        builder.storagePath("./target/" + name);
        Storage storage = builder.openStorage();
        return storage;
    }
}
