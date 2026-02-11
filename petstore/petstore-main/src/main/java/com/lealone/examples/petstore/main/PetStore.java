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
package com.lealone.examples.petstore.main;

import java.io.File;
import java.net.URL;

import com.lealone.main.Lealone;

public class PetStore {

    public static void main(String[] args) {
        // 请在浏览器中打开下面这个URL进行测试:
        // http://localhost:8080/
        Lealone.main(args);
    }

    public static String getAppBaseDir() {
        String dir;
        try {
            dir = System.getProperty("appBaseDir");
            if (dir == null) {
                String name = PetStore.class.getName().replace('.', '/') + ".class";
                URL url = PetStore.class.getClassLoader().getResource(name);
                String file = new File(url.toURI()).getAbsolutePath();
                int pos = file.indexOf("petstore-main");
                dir = file.substring(0, pos - 1);
            }
        } catch (Exception e) {
            dir = ".";
        }
        return dir;
    }

    public static String getAbsolutePath(String name) {
        return new File(getAppBaseDir(), name).getAbsolutePath().replace('/', File.separatorChar);
    }
}
