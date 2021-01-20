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
package org.lealone.examples.h2webconsole.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.h2.bnf.context.DbColumn;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.lealone.examples.h2webconsole.service.generated.OpsService;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

public class OpsServiceImpl extends ServiceImpl implements OpsService {

    private static final Comparator<DbTableOrView> SYSTEM_SCHEMA_COMPARATOR = Comparator
            .comparing(DbTableOrView::getName, String.CASE_INSENSITIVE_ORDER);

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
            Connection conn = DriverManager.getConnection(url, prop);
            ServiceSession session = ServiceConfig.instance.createNewSession(null);
            session.setConnection(conn);
            session.put("url", url);
            return session.get("sessionId").toString();
        } catch (SQLException e) {
            throw new RuntimeException("failed to login: " + e.getMessage(), e);
        }
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

    @Override
    public String tables(String jsessionid) {
        session = ServiceConfig.instance.getSession(jsessionid);
        DbContents contents = session.getContents();
        boolean isH2 = false;
        try {
            String url = (String) session.get("url");
            Connection conn = session.getConnection();
            contents.readContents(url, conn);
            session.loadBnf();
            isH2 = contents.isH2();

            session.addNode(0, 0, 0, "database", url, null);
            StringBuilder buff = new StringBuilder().append("setNode(0, 0, 0, 'database', '")
                    .append(escapeJavaScript(url)).append("', null);\n");
            int treeIndex = 1;

            DbSchema defaultSchema = contents.getDefaultSchema();
            treeIndex = addTablesAndViews(defaultSchema, true, buff, treeIndex);
            DbSchema[] schemas = contents.getSchemas();
            for (DbSchema schema : schemas) {
                if (schema == defaultSchema || schema == null) {
                    continue;
                }
                session.addNode(treeIndex, 0, 1, "folder", schema.name, null);
                buff.append("setNode(").append(treeIndex).append(", 0, 1, 'folder', '")
                        .append(escapeJavaScript(schema.name)).append("', null);\n");
                treeIndex++;
                treeIndex = addTablesAndViews(schema, false, buff, treeIndex);
            }
            if (isH2) {
                try (Statement stat = conn.createStatement()) {
                    ResultSet rs;
                    try {
                        rs = stat.executeQuery("SELECT SEQUENCE_NAME, BASE_VALUE, INCREMENT FROM "
                                + "INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
                    } catch (SQLException e) {
                        rs = stat.executeQuery("SELECT SEQUENCE_NAME, CURRENT_VALUE, INCREMENT FROM "
                                + "INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
                    }
                    for (int i = 0; rs.next(); i++) {
                        if (i == 0) {
                            buff.append("setNode(").append(treeIndex)
                                    .append(", 0, 1, 'sequences', '${text.tree.sequences}', null);\n");
                            treeIndex++;
                        }
                        String name = rs.getString(1);
                        String currentBase = rs.getString(2);
                        String increment = rs.getString(3);
                        session.addNode(treeIndex, 1, 1, "sequence", name, null);
                        buff.append("setNode(").append(treeIndex).append(", 1, 1, 'sequence', '")
                                .append(escapeJavaScript(name)).append("', null);\n");
                        treeIndex++;
                        session.addNode(treeIndex, 2, 2, "type", session.i18n("text.tree.current"), null);
                        buff.append("setNode(").append(treeIndex).append(", 2, 2, 'type', '${text.tree.current}: ")
                                .append(escapeJavaScript(currentBase)).append("', null);\n");
                        treeIndex++;
                        if (!"1".equals(increment)) {
                            session.addNode(treeIndex, 2, 2, "type", session.i18n("text.tree.increment"), null);
                            buff.append("setNode(").append(treeIndex)
                                    .append(", 2, 2, 'type', '${text.tree.increment}: ")
                                    .append(escapeJavaScript(increment)).append("', null);\n");
                            treeIndex++;
                        }
                    }
                    rs.close();
                    try {
                        rs = stat.executeQuery(
                                "SELECT USER_NAME, IS_ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY USER_NAME");
                    } catch (SQLException e) {
                        rs = stat.executeQuery("SELECT NAME, ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY NAME");
                    }
                    for (int i = 0; rs.next(); i++) {
                        if (i == 0) {
                            session.addNode(treeIndex, 0, 1, "users", session.i18n("text.tree.users"), null);
                            buff.append("setNode(").append(treeIndex)
                                    .append(", 0, 1, 'users', '${text.tree.users}', null);\n");
                            treeIndex++;
                        }
                        String name = rs.getString(1);
                        String admin = rs.getString(2);
                        session.addNode(treeIndex, 1, 1, "user", name, null);
                        buff.append("setNode(").append(treeIndex).append(", 1, 1, 'user', '")
                                .append(escapeJavaScript(name)).append("', null);\n");
                        treeIndex++;
                        if (admin.equalsIgnoreCase("TRUE")) {
                            session.addNode(treeIndex, 2, 2, "type", session.i18n("text.tree.admin"), null);
                            buff.append("setNode(").append(treeIndex)
                                    .append(", 2, 2, 'type', '${text.tree.admin}', null);\n");
                            treeIndex++;
                        }
                    }
                    rs.close();
                }
            }
            DatabaseMetaData meta = session.getMetaData();
            String version = meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion();
            session.addNode(treeIndex, 0, 0, "info", version, null);
            buff.append("setNode(").append(treeIndex).append(", 0, 0, 'info', '").append(escapeJavaScript(version))
                    .append("', null);\n").append("refreshQueryTables();");
            session.put("tree", buff.toString());
        } catch (Exception e) {
            session.put("tree", "");
            session.put("error", getStackTrace(0, e, isH2));
        }
        if (!session.tableList.isEmpty()) {
            JsonObject json = new JsonObject();
            json.put("tables", new JsonArray(session.tableList));
            json.put("nodes", new JsonArray(session.nodeList));
            String str = json.encode();
            session.tableList.clear();
            session.nodeList.clear();
            return str;
        }
        return session.get("tree").toString();
    }

    private int addTablesAndViews(DbSchema schema, boolean mainSchema, StringBuilder builder, int treeIndex)
            throws SQLException {
        if (schema == null) {
            return treeIndex;
        }
        Connection conn = session.getConnection();
        DatabaseMetaData meta = session.getMetaData();
        int level = mainSchema ? 0 : 1;
        boolean showColumns = mainSchema || !schema.isSystem;
        String indentation = ", " + level + ", " + (showColumns ? "1" : "2") + ", ";
        String indentNode = ", " + (level + 1) + ", 2, ";
        DbTableOrView[] tables = schema.getTables();
        if (tables == null) {
            return treeIndex;
        }
        DbContents contents = schema.getContents();
        boolean isOracle = contents.isOracle();
        boolean notManyTables = tables.length < SysProperties.CONSOLE_MAX_TABLES_LIST_INDEXES;
        try (PreparedStatement prep = showColumns ? prepareViewDefinitionQuery(conn, contents) : null) {
            if (prep != null) {
                prep.setString(1, schema.name);
            }
            if (schema.isSystem) {
                Arrays.sort(tables, SYSTEM_SCHEMA_COMPARATOR);
                for (DbTableOrView table : tables) {
                    treeIndex = addTableOrView(schema, mainSchema, builder, treeIndex, meta, false, indentation,
                            isOracle, notManyTables, table, table.isView(), prep, indentNode);
                }
            } else {
                for (DbTableOrView table : tables) {
                    if (table.isView()) {
                        continue;
                    }
                    treeIndex = addTableOrView(schema, mainSchema, builder, treeIndex, meta, showColumns, indentation,
                            isOracle, notManyTables, table, false, null, indentNode);
                }
                for (DbTableOrView table : tables) {
                    if (!table.isView()) {
                        continue;
                    }
                    treeIndex = addTableOrView(schema, mainSchema, builder, treeIndex, meta, showColumns, indentation,
                            isOracle, notManyTables, table, true, prep, indentNode);
                }
            }
        }
        return treeIndex;
    }

    private static PreparedStatement prepareViewDefinitionQuery(Connection conn, DbContents contents) {
        if (contents.mayHaveStandardViews()) {
            try {
                return conn.prepareStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS"
                        + " WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
            } catch (SQLException e) {
                contents.setMayHaveStandardViews(false);
            }
        }
        return null;
    }

    private int addTableOrView(DbSchema schema, boolean mainSchema, StringBuilder builder, int treeIndex,
            DatabaseMetaData meta, boolean showColumns, String indentation, boolean isOracle, boolean notManyTables,
            DbTableOrView table, boolean isView, PreparedStatement prep, String indentNode) throws SQLException {
        int tableId = treeIndex;
        String tab = table.getQuotedName();
        if (!mainSchema) {
            tab = schema.quotedName + '.' + tab;
        }
        tab = escapeIdentifier(tab);
        String[] a = indentation.split(",");
        session.addNode(treeIndex, Integer.parseInt(a[1].trim()), Integer.parseInt(a[2].trim()),
                isView ? "view" : "table", table.getName(), tab);
        builder.append("setNode(").append(treeIndex).append(indentation).append(" '").append(isView ? "view" : "table")
                .append("', '").append(escapeJavaScript(table.getName())).append("', 'javascript:ins(\\'").append(tab)
                .append("\\',true)');\n");
        treeIndex++;
        if (showColumns) {
            StringBuilder columnsBuilder = new StringBuilder();
            treeIndex = addColumns(mainSchema, table, builder, treeIndex, notManyTables, columnsBuilder);
            if (isView) {
                if (prep != null) {
                    prep.setString(2, table.getName());
                    try (ResultSet rs = prep.executeQuery()) {
                        if (rs.next()) {
                            String sql = rs.getString(1);
                            if (sql != null) {
                                a = indentNode.split(",");
                                session.addNode(treeIndex, Integer.parseInt(a[1].trim()), Integer.parseInt(a[2].trim()),
                                        "type", sql, null);
                                builder.append("setNode(").append(treeIndex).append(indentNode).append(" 'type', '")
                                        .append(escapeJavaScript(sql)).append("', null);\n");
                                treeIndex++;
                            }
                        }
                    }
                }
            } else if (!isOracle && notManyTables) {
                treeIndex = addIndexes(mainSchema, meta, table.getName(), schema.name, builder, treeIndex);
            }
            session.addTable(table.getName(), columnsBuilder.toString(), tableId);
            builder.append("addTable('").append(escapeJavaScript(table.getName())).append("', '")
                    .append(escapeJavaScript(columnsBuilder.toString())).append("', ").append(tableId).append(");\n");
        }
        return treeIndex;
    }

    private int addColumns(boolean mainSchema, DbTableOrView table, StringBuilder builder, int treeIndex,
            boolean showColumnTypes, StringBuilder columnsBuilder) {
        DbColumn[] columns = table.getColumns();
        for (int i = 0; columns != null && i < columns.length; i++) {
            DbColumn column = columns[i];
            if (columnsBuilder.length() > 0) {
                columnsBuilder.append(' ');
            }
            columnsBuilder.append(column.getName());
            String col = escapeIdentifier(column.getName());
            String level = mainSchema ? ", 1, 1" : ", 2, 2";
            String[] a = level.split(",");
            session.addNode(treeIndex, Integer.parseInt(a[1].trim()), Integer.parseInt(a[2].trim()), "column",
                    column.getName(), col);
            builder.append("setNode(").append(treeIndex).append(level).append(", 'column', '")
                    .append(escapeJavaScript(column.getName())).append("', 'javascript:ins(\\'").append(col)
                    .append("\\')');\n");
            treeIndex++;
            if (mainSchema && showColumnTypes) {
                session.addNode(treeIndex, 2, 2, "type", column.getDataType(), null);
                builder.append("setNode(").append(treeIndex).append(", 2, 2, 'type', '")
                        .append(escapeJavaScript(column.getDataType())).append("', null);\n");
                treeIndex++;
            }
        }
        return treeIndex;
    }

    private static String escapeIdentifier(String name) {
        return StringUtils.urlEncode(escapeJavaScript(name)).replace('+', ' ');
    }

    /**
     * This class represents index information for the GUI.
     */
    static class IndexInfo {

        /**
         * The index name.
         */
        String name;

        /**
         * The index type name.
         */
        String type;

        /**
         * The indexed columns.
         */
        String columns;
    }

    private int addIndexes(boolean mainSchema, DatabaseMetaData meta, String table, String schema, StringBuilder buff,
            int treeIndex) throws SQLException {
        ResultSet rs;
        try {
            rs = meta.getIndexInfo(null, schema, table, false, true);
        } catch (SQLException e) {
            // SQLite
            return treeIndex;
        }
        HashMap<String, IndexInfo> indexMap = new HashMap<>();
        while (rs.next()) {
            String name = rs.getString("INDEX_NAME");
            IndexInfo info = indexMap.get(name);
            if (info == null) {
                int t = rs.getInt("TYPE");
                String type;
                if (t == DatabaseMetaData.tableIndexClustered) {
                    type = "";
                } else if (t == DatabaseMetaData.tableIndexHashed) {
                    type = " (${text.tree.hashed})";
                } else if (t == DatabaseMetaData.tableIndexOther) {
                    type = "";
                } else {
                    type = null;
                }
                if (name != null && type != null) {
                    info = new IndexInfo();
                    info.name = name;
                    type = (rs.getBoolean("NON_UNIQUE") ? "${text.tree.nonUnique}" : "${text.tree.unique}") + type;
                    info.type = type;
                    info.columns = rs.getString("COLUMN_NAME");
                    indexMap.put(name, info);
                }
            } else {
                info.columns += ", " + rs.getString("COLUMN_NAME");
            }
        }
        rs.close();
        if (indexMap.size() > 0) {
            String level = mainSchema ? ", 1, 1" : ", 2, 1";
            String levelIndex = mainSchema ? ", 2, 1" : ", 3, 1";
            String levelColumnType = mainSchema ? ", 3, 2" : ", 4, 2";
            String[] a = level.split(",");
            session.addNode(treeIndex, Integer.parseInt(a[1].trim()), Integer.parseInt(a[2].trim()), "index_az",
                    session.i18n("text.tree.indexes"), null);
            buff.append("setNode(").append(treeIndex).append(level)
                    .append(", 'index_az', '${text.tree.indexes}', null);\n");
            treeIndex++;
            String[] a1 = levelIndex.split(",");
            String[] a2 = levelColumnType.split(",");
            for (IndexInfo info : indexMap.values()) {
                session.addNode(treeIndex, Integer.parseInt(a1[1].trim()), Integer.parseInt(a1[2].trim()), "index",
                        info.name, null);
                buff.append("setNode(").append(treeIndex).append(levelIndex).append(", 'index', '")
                        .append(escapeJavaScript(info.name)).append("', null);\n");
                treeIndex++;
                session.addNode(treeIndex, Integer.parseInt(a2[1].trim()), Integer.parseInt(a2[2].trim()), "type",
                        info.type, null);
                buff.append("setNode(").append(treeIndex).append(levelColumnType).append(", 'type', '")
                        .append(info.type).append("', null);\n");
                treeIndex++;
                session.addNode(treeIndex, Integer.parseInt(a2[1].trim()), Integer.parseInt(a2[2].trim()), "type",
                        info.columns, null);
                buff.append("setNode(").append(treeIndex).append(levelColumnType).append(", 'type', '")
                        .append(escapeJavaScript(info.columns)).append("', null);\n");
                treeIndex++;
            }
        }
        return treeIndex;
    }

    /**
     * Escape text as a the javascript string.
     *
     * @param s the text
     * @return the javascript string
     */
    static String escapeJavaScript(String s) {
        if (s == null) {
            return null;
        }
        int length = s.length();
        if (length == 0) {
            return "";
        }
        StringBuilder buff = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            switch (c) {
            case '"':
                buff.append("\\\"");
                break;
            case '\'':
                buff.append("\\'");
                break;
            case '\\':
                buff.append("\\\\");
                break;
            case '\n':
                buff.append("\\n");
                break;
            case '\r':
                buff.append("\\r");
                break;
            case '\t':
                buff.append("\\t");
                break;
            default:
                buff.append(c);
                break;
            }
        }
        return buff.toString();
    }
}
