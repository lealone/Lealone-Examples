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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.lealone.common.util.IOUtils;
import org.lealone.db.Constants;

public class JsoupTest {

    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.parse(new File("../petstore-web/web/store/index.html"), "utf-8");
        System.out.println(doc.title());

        Elements elements = doc.select("[v-replace]");
        for (Element e : elements) {
            System.out.println(e.attr("v-replace"));
            File f = new File("../petstore-web/web" + e.attr("v-replace"));
            Document d = Jsoup.parse(f, "utf-8");
            for (Node n : d.body().childNodes()) {
                if (n instanceof Element) {
                    e.replaceWith(n);
                    break;
                }
            }
        }

        elements = doc.select("[v-insert]");
        for (Element e : elements) {
            System.out.println(e.attr("v-insert"));
            File f = new File("../petstore-web/web" + e.attr("v-insert"));
            e.removeAttr("v-insert");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), Constants.UTF8));
            String str = IOUtils.readStringAndClose(reader, -1);
            e.append(str);
        }

        System.out.println(doc.html());
    }

}
