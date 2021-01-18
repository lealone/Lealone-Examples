/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.lealone.examples.h2webconsole.web.deprecated;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.h2.api.ErrorCode;
import org.h2.bnf.Bnf;
import org.h2.bnf.context.DbColumn;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcException;
import org.h2.util.JdbcUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.Profiler;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;

/**
 * For each connection to a session, an object of this class is created.
 * This class is used by the H2 Console.
 */
public class WebHandler {

    private static final Comparator<DbTableOrView> SYSTEM_SCHEMA_COMPARATOR = Comparator
            .comparing(DbTableOrView::getName, String.CASE_INSENSITIVE_ORDER);

    /**
     * The web server.
     */
    protected final WebServer server;

    /**
     * The session.
     */
    protected WebSession session;

    /**
     * The session attributes
     */
    protected Properties attributes;

    /**
     * The language in the HTTP header.
     */
    protected String headerLanguage;

    WebHandler(WebServer server) {
        this.server = server;
    }

    private static String getComboBox(String[] elements, String selected) {
        StringBuilder buff = new StringBuilder();
        for (String value : elements) {
            buff.append("<option value=\"").append(PageParser.escapeHtmlData(value)).append('\"');
            if (value.equals(selected)) {
                buff.append(" selected");
            }
            buff.append('>').append(PageParser.escapeHtml(value)).append("</option>");
        }
        return buff.toString();
    }

    private static String getComboBox(String[][] elements, String selected) {
        StringBuilder buff = new StringBuilder();
        for (String[] n : elements) {
            buff.append("<option value=\"").append(PageParser.escapeHtmlData(n[0])).append('\"');
            if (n[0].equals(selected)) {
                buff.append(" selected");
            }
            buff.append('>').append(PageParser.escapeHtml(n[1])).append("</option>");
        }
        return buff.toString();
    }

    boolean checkAdmin(String file) {
        Boolean b = (Boolean) session.get("admin");
        if (b != null && b) {
            return true;
        }
        String key = server.getKey();
        if (key != null && key.equals(session.get("key"))) {
            return true;
        }
        session.put("adminBack", file);
        return false;
    }

    String autoCompleteList() {
        String query = (String) attributes.get("query");
        boolean lowercase = false;
        String tQuery = query.trim();
        if (!tQuery.isEmpty() && Character.isLowerCase(tQuery.charAt(0))) {
            lowercase = true;
        }
        try {
            String sql = query;
            if (sql.endsWith(";")) {
                sql += " ";
            }
            ScriptReader reader = new ScriptReader(new StringReader(sql));
            reader.setSkipRemarks(true);
            String lastSql = "";
            while (true) {
                String n = reader.readStatement();
                if (n == null) {
                    break;
                }
                lastSql = n;
            }
            String result = "";
            if (reader.isInsideRemark()) {
                if (reader.isBlockRemark()) {
                    result = "1#(End Remark)# */\n" + result;
                } else {
                    result = "1#(Newline)#\n" + result;
                }
            } else {
                sql = lastSql;
                while (sql.length() > 0 && sql.charAt(0) <= ' ') {
                    sql = sql.substring(1);
                }
                String tSql = sql.trim();
                if (!tSql.isEmpty() && Character.isLowerCase(tSql.charAt(0))) {
                    lowercase = true;
                }
                Bnf bnf = session.getBnf();
                if (bnf == null) {
                    return "autoCompleteList.jsp";
                }
                HashMap<String, String> map = bnf.getNextTokenList(sql);
                String space = "";
                if (sql.length() > 0) {
                    char last = sql.charAt(sql.length() - 1);
                    if (!Character.isWhitespace(last) && (last != '.' && last >= ' ' && last != '\'' && last != '"')) {
                        space = " ";
                    }
                }
                ArrayList<String> list = new ArrayList<>(map.size());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String type = String.valueOf(key.charAt(0));
                    if (Integer.parseInt(type) > 2) {
                        continue;
                    }
                    key = key.substring(2);
                    if (Character.isLetter(key.charAt(0)) && lowercase) {
                        key = StringUtils.toLowerEnglish(key);
                        value = StringUtils.toLowerEnglish(value);
                    }
                    if (key.equals(value) && !".".equals(value)) {
                        value = space + value;
                    }
                    key = StringUtils.urlEncode(key);
                    key = key.replace('+', ' ');
                    value = StringUtils.urlEncode(value);
                    value = value.replace('+', ' ');
                    list.add(type + "#" + key + "#" + value);
                }
                Collections.sort(list);
                if (query.endsWith("\n") || tQuery.endsWith(";")) {
                    list.add(0, "1#(Newline)#\n");
                }
                result = StringUtils.join(new StringBuilder(), list, "|").toString();
            }
            session.put("autoCompleteList", result);
        } catch (Throwable e) {
            server.traceError(e);
        }
        return "autoCompleteList.jsp";
    }

    String index() {
        String[][] languageArray = WebServer.LANGUAGES;
        String language = (String) attributes.get("language");
        Locale locale = session.locale;
        if (language != null) {
            if (locale == null || !StringUtils.toLowerEnglish(locale.getLanguage()).equals(language)) {
                locale = new Locale(language, "");
                server.readTranslations(session, locale.getLanguage());
                session.put("language", language);
                session.locale = locale;
            }
        } else {
            language = (String) session.get("language");
        }
        if (language == null) {
            // if the language is not yet known
            // use the last header
            language = headerLanguage;
        }
        session.put("languageCombo", getComboBox(languageArray, language));
        String[] settingNames = server.getSettingNames();
        String setting = attributes.getProperty("setting");
        if (setting == null && settingNames.length > 0) {
            setting = settingNames[0];
        }
        String combobox = getComboBox(settingNames, setting);
        session.put("settingsList", combobox);
        ConnectionInfo info = server.getSetting(setting);
        if (info == null) {
            info = new ConnectionInfo();
        }
        session.put("setting", PageParser.escapeHtmlData(setting));
        session.put("name", PageParser.escapeHtmlData(setting));
        session.put("driver", PageParser.escapeHtmlData(info.driver));
        session.put("url", PageParser.escapeHtmlData(info.url));
        session.put("user", PageParser.escapeHtmlData(info.user));
        return "index.jsp";
    }

    String getHistory() {
        int id = Integer.parseInt(attributes.getProperty("id"));
        String sql = session.getCommand(id);
        session.put("query", PageParser.escapeHtmlData(sql));
        return "query.jsp";
    }

    private static int addColumns(boolean mainSchema, DbTableOrView table, StringBuilder builder, int treeIndex,
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
            builder.append("setNode(").append(treeIndex).append(level).append(", 'column', '")
                    .append(PageParser.escapeJavaScript(column.getName())).append("', 'javascript:ins(\\'").append(col)
                    .append("\\')');\n");
            treeIndex++;
            if (mainSchema && showColumnTypes) {
                builder.append("setNode(").append(treeIndex).append(", 2, 2, 'type', '")
                        .append(PageParser.escapeJavaScript(column.getDataType())).append("', null);\n");
                treeIndex++;
            }
        }
        return treeIndex;
    }

    private static String escapeIdentifier(String name) {
        return StringUtils.urlEncode(PageParser.escapeJavaScript(name)).replace('+', ' ');
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

    private static int addIndexes(boolean mainSchema, DatabaseMetaData meta, String table, String schema,
            StringBuilder buff, int treeIndex) throws SQLException {
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
            buff.append("setNode(").append(treeIndex).append(level)
                    .append(", 'index_az', '${text.tree.indexes}', null);\n");
            treeIndex++;
            for (IndexInfo info : indexMap.values()) {
                buff.append("setNode(").append(treeIndex).append(levelIndex).append(", 'index', '")
                        .append(PageParser.escapeJavaScript(info.name)).append("', null);\n");
                treeIndex++;
                buff.append("setNode(").append(treeIndex).append(levelColumnType).append(", 'type', '")
                        .append(info.type).append("', null);\n");
                treeIndex++;
                buff.append("setNode(").append(treeIndex).append(levelColumnType).append(", 'type', '")
                        .append(PageParser.escapeJavaScript(info.columns)).append("', null);\n");
                treeIndex++;
            }
        }
        return treeIndex;
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

    private static int addTableOrView(DbSchema schema, boolean mainSchema, StringBuilder builder, int treeIndex,
            DatabaseMetaData meta, boolean showColumns, String indentation, boolean isOracle, boolean notManyTables,
            DbTableOrView table, boolean isView, PreparedStatement prep, String indentNode) throws SQLException {
        int tableId = treeIndex;
        String tab = table.getQuotedName();
        if (!mainSchema) {
            tab = schema.quotedName + '.' + tab;
        }
        tab = escapeIdentifier(tab);
        builder.append("setNode(").append(treeIndex).append(indentation).append(" '").append(isView ? "view" : "table")
                .append("', '").append(PageParser.escapeJavaScript(table.getName())).append("', 'javascript:ins(\\'")
                .append(tab).append("\\',true)');\n");
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
                                builder.append("setNode(").append(treeIndex).append(indentNode).append(" 'type', '")
                                        .append(PageParser.escapeJavaScript(sql)).append("', null);\n");
                                treeIndex++;
                            }
                        }
                    }
                }
            } else if (!isOracle && notManyTables) {
                treeIndex = addIndexes(mainSchema, meta, table.getName(), schema.name, builder, treeIndex);
            }
            builder.append("addTable('").append(PageParser.escapeJavaScript(table.getName())).append("', '")
                    .append(PageParser.escapeJavaScript(columnsBuilder.toString())).append("', ").append(tableId)
                    .append(");\n");
        }
        return treeIndex;
    }

    String tables() {
        DbContents contents = session.getContents();
        boolean isH2 = false;
        try {
            String url = (String) session.get("url");
            Connection conn = session.getConnection();
            contents.readContents(url, conn);
            session.loadBnf();
            isH2 = contents.isH2();

            StringBuilder buff = new StringBuilder().append("setNode(0, 0, 0, 'database', '")
                    .append(PageParser.escapeJavaScript(url)).append("', null);\n");
            int treeIndex = 1;

            DbSchema defaultSchema = contents.getDefaultSchema();
            treeIndex = addTablesAndViews(defaultSchema, true, buff, treeIndex);
            DbSchema[] schemas = contents.getSchemas();
            for (DbSchema schema : schemas) {
                if (schema == defaultSchema || schema == null) {
                    continue;
                }
                buff.append("setNode(").append(treeIndex).append(", 0, 1, 'folder', '")
                        .append(PageParser.escapeJavaScript(schema.name)).append("', null);\n");
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
                        buff.append("setNode(").append(treeIndex).append(", 1, 1, 'sequence', '")
                                .append(PageParser.escapeJavaScript(name)).append("', null);\n");
                        treeIndex++;
                        buff.append("setNode(").append(treeIndex).append(", 2, 2, 'type', '${text.tree.current}: ")
                                .append(PageParser.escapeJavaScript(currentBase)).append("', null);\n");
                        treeIndex++;
                        if (!"1".equals(increment)) {
                            buff.append("setNode(").append(treeIndex)
                                    .append(", 2, 2, 'type', '${text.tree.increment}: ")
                                    .append(PageParser.escapeJavaScript(increment)).append("', null);\n");
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
                            buff.append("setNode(").append(treeIndex)
                                    .append(", 0, 1, 'users', '${text.tree.users}', null);\n");
                            treeIndex++;
                        }
                        String name = rs.getString(1);
                        String admin = rs.getString(2);
                        buff.append("setNode(").append(treeIndex).append(", 1, 1, 'user', '")
                                .append(PageParser.escapeJavaScript(name)).append("', null);\n");
                        treeIndex++;
                        if (admin.equalsIgnoreCase("TRUE")) {
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
            buff.append("setNode(").append(treeIndex).append(", 0, 0, 'info', '")
                    .append(PageParser.escapeJavaScript(version)).append("', null);\n").append("refreshQueryTables();");
            session.put("tree", buff.toString());
        } catch (Exception e) {
            session.put("tree", "");
            session.put("error", getStackTrace(0, e, isH2));
        }
        return "tables.jsp";
    }

    String getStackTrace(int id, Throwable e, boolean isH2) {
        try {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            String stackTrace = writer.toString();
            stackTrace = PageParser.escapeHtml(stackTrace);
            if (isH2) {
                stackTrace = linkToSource(stackTrace);
            }
            stackTrace = StringUtils.replaceAll(stackTrace, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            String message = PageParser.escapeHtml(e.getMessage());
            String error = "<a class=\"error\" href=\"#\" " + "onclick=\"var x=document.getElementById('st" + id
                    + "').style;x.display=x.display==''?'none':'';\">" + message + "</a>";
            if (e instanceof SQLException) {
                SQLException se = (SQLException) e;
                error += " " + se.getSQLState() + "/" + se.getErrorCode();
                if (isH2) {
                    int code = se.getErrorCode();
                    error += " <a href=\"https://h2database.com/javadoc/" + "org/h2/api/ErrorCode.html#c" + code
                            + "\">(${text.a.help})</a>";
                }
            }
            error += "<span style=\"display: none;\" id=\"st" + id + "\"><br />" + stackTrace + "</span>";
            error = formatAsError(error);
            return error;
        } catch (OutOfMemoryError e2) {
            server.traceError(e);
            return e.toString();
        }
    }

    private static String linkToSource(String s) {
        try {
            StringBuilder result = new StringBuilder(s.length());
            int idx = s.indexOf("<br />");
            result.append(s, 0, idx);
            while (true) {
                int start = s.indexOf("org.h2.", idx);
                if (start < 0) {
                    result.append(s.substring(idx));
                    break;
                }
                result.append(s, idx, start);
                int end = s.indexOf(')', start);
                if (end < 0) {
                    result.append(s.substring(idx));
                    break;
                }
                String element = s.substring(start, end);
                int open = element.lastIndexOf('(');
                int dotMethod = element.lastIndexOf('.', open - 1);
                int dotClass = element.lastIndexOf('.', dotMethod - 1);
                String packageName = element.substring(0, dotClass);
                int colon = element.lastIndexOf(':');
                String file = element.substring(open + 1, colon);
                String lineNumber = element.substring(colon + 1, element.length());
                String fullFileName = packageName.replace('.', '/') + "/" + file;
                result.append("<a href=\"https://h2database.com/html/source.html?file=");
                result.append(fullFileName);
                result.append("&line=");
                result.append(lineNumber);
                result.append("&build=");
                result.append(Constants.BUILD_ID);
                result.append("\">");
                result.append(element);
                result.append("</a>");
                idx = end;
            }
            return result.toString();
        } catch (Throwable t) {
            return s;
        }
    }

    static String formatAsError(String s) {
        return "<div class=\"error\">" + s + "</div>";
    }

    String test(NetworkConnectionInfo networkConnectionInfo) {
        String driver = attributes.getProperty("driver", "");
        String url = attributes.getProperty("url", "");
        String user = attributes.getProperty("user", "");
        String password = attributes.getProperty("password", "");
        session.put("driver", driver);
        session.put("url", url);
        session.put("user", user);
        boolean isH2 = url.startsWith("jdbc:h2:");
        try {
            long start = System.currentTimeMillis();
            String profOpen = "", profClose = "";
            Profiler prof = new Profiler();
            prof.startCollecting();
            Connection conn;
            try {
                conn = server.getConnection(driver, url, user, password, null, networkConnectionInfo);
            } finally {
                prof.stopCollecting();
                profOpen = prof.getTop(3);
            }
            prof = new Profiler();
            prof.startCollecting();
            try {
                JdbcUtils.closeSilently(conn);
            } finally {
                prof.stopCollecting();
                profClose = prof.getTop(3);
            }
            long time = System.currentTimeMillis() - start;
            String success;
            if (time > 1000) {
                success = "<a class=\"error\" href=\"#\" " + "onclick=\"var x=document.getElementById('prof').style;x."
                        + "display=x.display==''?'none':'';\">" + "${text.login.testSuccessful}</a>"
                        + "<span style=\"display: none;\" id=\"prof\"><br />" + PageParser.escapeHtml(profOpen)
                        + "<br />" + PageParser.escapeHtml(profClose) + "</span>";
            } else {
                success = "<div class=\"success\">${text.login.testSuccessful}</div>";
            }
            session.put("error", success);
            // session.put("error", "${text.login.testSuccessful}");
            return "login.jsp";
        } catch (Exception e) {
            session.put("error", getLoginError(e, isH2));
            return "login.jsp";
        }
    }

    /**
     * Get the formatted login error message.
     *
     * @param e the exception
     * @param isH2 if the current database is a H2 database
     * @return the formatted error message
     */
    private String getLoginError(Exception e, boolean isH2) {
        if (e instanceof JdbcException && ((JdbcException) e).getErrorCode() == ErrorCode.CLASS_NOT_FOUND_1) {
            return "${text.login.driverNotFound}<br />" + getStackTrace(0, e, isH2);
        }
        return getStackTrace(0, e, isH2);
    }

    String login(NetworkConnectionInfo networkConnectionInfo) {
        String driver = attributes.getProperty("driver", "");
        String url = attributes.getProperty("url", "");
        String user = attributes.getProperty("user", "");
        String password = attributes.getProperty("password", "");
        session.put("autoCommit", "checked");
        session.put("autoComplete", "1");
        session.put("maxrows", "1000");
        boolean isH2 = url.startsWith("jdbc:h2:");
        try {
            Connection conn = server.getConnection(driver, url, user, password, (String) session.get("key"),
                    networkConnectionInfo);
            session.setConnection(conn);
            session.put("url", url);
            session.put("user", user);
            session.remove("error");
            settingSave();
            return "frame.jsp";
        } catch (Exception e) {
            session.put("error", getLoginError(e, isH2));
            return "login.jsp";
        }
    }

    String logout() {
        try {
            Connection conn = session.getConnection();
            session.setConnection(null);
            session.remove("conn");
            session.remove("result");
            session.remove("tables");
            session.remove("user");
            session.remove("tool");
            if (conn != null) {
                if (session.getShutdownServerOnDisconnect()) {
                    server.shutdown();
                } else {
                    conn.close();
                }
            }
        } catch (Exception e) {
            trace(e.toString());
        }
        session.remove("admin");
        return "index.do";
    }

    /**
    * Save the current connection settings to the properties file.
    *
    * @return the file to open afterwards
    */
    String settingSave() {
        ConnectionInfo info = new ConnectionInfo();
        info.name = attributes.getProperty("name", "");
        info.driver = attributes.getProperty("driver", "");
        info.url = attributes.getProperty("url", "");
        info.user = attributes.getProperty("user", "");
        server.updateSetting(info);
        attributes.put("setting", info.name);
        server.saveProperties(null);
        return "index.do";
    }

    String settingRemove() {
        String setting = attributes.getProperty("name", "");
        server.removeSetting(setting);
        ArrayList<ConnectionInfo> settings = server.getSettings();
        if (!settings.isEmpty()) {
            attributes.put("setting", settings.get(0));
        }
        server.saveProperties(null);
        return "index.do";
    }

    void trace(String s) {
        server.trace(s);
    }

}
