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
package org.lealone.opscenter.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.h2.message.DbException;
import org.h2.util.SortedProperties;
import org.lealone.opscenter.service.generated.OpsService;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

public class OpsServiceImpl implements OpsService {
    static final String[][] LANGUAGES = { //
            { "cs", "\u010ce\u0161tina" }, //
            { "de", "Deutsch" }, //
            { "en", "English" }, //
            { "es", "Espa\u00f1ol" }, //
            { "fr", "Fran\u00e7ais" }, //
            { "hi", "Hindi \u0939\u093f\u0902\u0926\u0940" }, //
            { "hu", "Magyar" }, //
            { "ko", "\ud55c\uad6d\uc5b4" }, //
            { "in", "Indonesia" }, //
            { "it", "Italiano" }, //
            { "ja", "\u65e5\u672c\u8a9e" }, //
            { "nl", "Nederlands" }, //
            { "pl", "Polski" }, //
            { "pt_BR", "Portugu\u00eas (Brasil)" }, //
            { "pt_PT", "Portugu\u00eas (Europeu)" }, //
            { "ru", "\u0440\u0443\u0441\u0441\u043a\u0438\u0439" }, //
            { "sk", "Slovensky" }, //
            { "tr", "T\u00fcrk\u00e7e" }, //
            { "uk", "\u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430" }, //
            { "zh_CN", "\u4e2d\u6587 (\u7b80\u4f53)" }, //
            { "zh_TW", "\u4e2d\u6587 (\u7e41\u9ad4)" }, //
    };

    @Override
    public String getLanguages() {
        ArrayList<List<String>> list = new ArrayList<>(LANGUAGES.length);
        for (int i = 0; i < LANGUAGES.length; i++) {
            list.add(Arrays.asList(LANGUAGES[i]));
        }
        JsonObject json = new JsonObject();
        json.put("languages", new JsonArray(list));
        return json.encode();
    }

    @Override
    public String getSettings(String setting) {
        JsonObject json = new JsonObject();
        String[] settingNames = ServiceConfig.instance.getSettingNames();
        if ((setting == null || setting.isEmpty()) && settingNames.length > 0) {
            setting = settingNames[0];
        }
        ConnectionInfo info = ServiceConfig.instance.getSetting(setting);
        if (info == null) {
            info = new ConnectionInfo();
        }
        json.put("settings", new JsonArray(Arrays.asList(settingNames)));
        json.put("setting", setting);
        json.put("name", setting);
        json.put("driver", info.driver);
        json.put("url", info.url);
        json.put("user", info.user);
        return json.encode();
    }

    @Override
    public String settingSave(String name, String driver, String url, String user) {
        ConnectionInfo info = new ConnectionInfo();
        info.name = name;
        info.driver = driver;
        info.url = url;
        info.user = user;
        ServiceConfig.instance.updateSetting(info);
        ServiceConfig.instance.saveProperties(null);
        return name;
    }

    @Override
    public String settingRemove(String name) {
        String setting = "";
        ServiceConfig.instance.removeSetting(name);
        ArrayList<ConnectionInfo> settings = ServiceConfig.instance.getSettings();
        if (!settings.isEmpty()) {
            setting = settings.get(0).name;
        }
        ServiceConfig.instance.saveProperties(null);
        return setting;
    }

    @Override
    public String login(String url, String user, String password) {
        Properties prop = new Properties();
        prop.setProperty("user", user);
        prop.setProperty("password", password);
        try {
            DriverManager.getConnection(url, prop);
        } catch (SQLException e) {
            throw new RuntimeException("failed to login: " + e.getMessage(), e);
        }
        return "ok";
    }

    @Override
    public String readTranslations(String language) {
        JsonObject json = new JsonObject();
        if (language == null)
            language = "zh_CN";
        ServiceConfig instance = ServiceConfig.instance;
        Locale locale = instance.getLocale(language);
        language = locale.toString();
        Map<String, Object> map = new HashMap<>();
        Properties text = new Properties();
        try {
            instance.trace("translation: " + language);
            byte[] trans = instance.getFile("_text_" + language.toLowerCase() + ".prop");
            instance.trace("  " + new String(trans));
            text = SortedProperties.fromLines(new String(trans, StandardCharsets.UTF_8));
            // remove starting # (if not translated yet)
            for (Entry<Object, Object> entry : text.entrySet()) {
                String value = (String) entry.getValue();
                if (value.startsWith("#")) {
                    entry.setValue(value.substring(1));
                }
                String key = entry.getKey().toString();
                map.put(key, entry.getValue());
            }
        } catch (IOException e) {
            DbException.traceThrowable(e);
        }
        map.put("language", language);
        json.put("text", new JsonObject(map));
        return json.encode();
    }

    @Override
    public String testConnection() {
        return null;
    }
}
