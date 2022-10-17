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

import org.lealone.examples.petstore.main.PetStore;
import org.lealone.plugins.service.template.TemplateEngine;

public class TemplateEngineTest {

    public static void main(String[] args) throws Exception {
        String webRoot = PetStore.getAbsolutePath("petstore-web/web");
        TemplateEngine te = new TemplateEngine(webRoot, "utf-8");
        String str = te.process("/home/index.html");
        System.out.println(str);
    }

}
