/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.lealone.examples.h2webconsole.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.h2.bnf.context.DbColumn;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;
import org.lealone.examples.h2webconsole.service.generated.DatabaseService;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

public class DatabaseServiceImpl extends ServiceImpl implements DatabaseService {

    private static final Comparator<DbTableOrView> SYSTEM_SCHEMA_COMPARATOR = Comparator
            .comparing(DbTableOrView::getName, String.CASE_INSENSITIVE_ORDER);

    @Override
    public String readAllDatabaseObjects(String jsessionid) {
        session = ServiceConfig.instance.getSession(jsessionid);
        DbContents contents = session.getContents();
        boolean isH2 = false;
        try {
            String url = (String) session.get("url");
            Connection conn = session.getConnection();
            contents.readContents(url, conn);
            session.loadBnf();
            isH2 = contents.isH2();

            session.addNode(0, 0, 0, "database", url);
            int treeIndex = 1;

            DbSchema defaultSchema = contents.getDefaultSchema();
            treeIndex = addTablesAndViews(defaultSchema, true, treeIndex);
            DbSchema[] schemas = contents.getSchemas();
            for (DbSchema schema : schemas) {
                if (schema == defaultSchema || schema == null) {
                    continue;
                }
                session.addNode(treeIndex, 0, 1, "folder", schema.name);
                treeIndex++;
                treeIndex = addTablesAndViews(schema, false, treeIndex);
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
                            session.addNode(treeIndex, 0, 1, "sequences", session.i18n("text.tree.sequences"));
                            treeIndex++;
                        }
                        String name = rs.getString(1);
                        String currentBase = rs.getString(2);
                        String increment = rs.getString(3);
                        session.addNode(treeIndex, 1, 1, "sequence", name);
                        treeIndex++;
                        session.addNode(treeIndex, 2, 2, "type",
                                session.i18n("text.tree.current") + ": " + currentBase);
                        treeIndex++;
                        if (!"1".equals(increment)) {
                            session.addNode(treeIndex, 2, 2, "type",
                                    session.i18n("text.tree.increment") + ": " + increment);
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
                            session.addNode(treeIndex, 0, 1, "users", session.i18n("text.tree.users"));
                            treeIndex++;
                        }
                        String name = rs.getString(1);
                        String admin = rs.getString(2);
                        session.addNode(treeIndex, 1, 1, "user", name);
                        treeIndex++;
                        if (admin.equalsIgnoreCase("TRUE")) {
                            session.addNode(treeIndex, 2, 2, "type", session.i18n("text.tree.admin"));
                            treeIndex++;
                        }
                    }
                    rs.close();
                }
            }
            DatabaseMetaData meta = session.getMetaData();
            String version = meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion();
            session.addNode(treeIndex, 0, 0, "info", version);
        } catch (Exception e) {
            session.put("error", getStackTrace(0, e, isH2));
        }
        JsonObject json = new JsonObject();
        json.put("tables", new JsonArray(session.tableList));
        json.put("nodes", new JsonArray(session.nodeList));
        String str = json.encode();
        session.tableList.clear();
        session.nodeList.clear();
        return str;
    }

    private int addTablesAndViews(DbSchema schema, boolean mainSchema, int treeIndex) throws SQLException {
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
                    treeIndex = addTableOrView(schema, mainSchema, treeIndex, meta, false, indentation, isOracle,
                            notManyTables, table, table.isView(), prep, indentNode);
                }
            } else {
                for (DbTableOrView table : tables) {
                    if (table.isView()) {
                        continue;
                    }
                    treeIndex = addTableOrView(schema, mainSchema, treeIndex, meta, showColumns, indentation, isOracle,
                            notManyTables, table, false, null, indentNode);
                }
                for (DbTableOrView table : tables) {
                    if (!table.isView()) {
                        continue;
                    }
                    treeIndex = addTableOrView(schema, mainSchema, treeIndex, meta, showColumns, indentation, isOracle,
                            notManyTables, table, true, prep, indentNode);
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

    private int addTableOrView(DbSchema schema, boolean mainSchema, int treeIndex, DatabaseMetaData meta,
            boolean showColumns, String indentation, boolean isOracle, boolean notManyTables, DbTableOrView table,
            boolean isView, PreparedStatement prep, String indentNode) throws SQLException {
        int tableId = treeIndex;
        String tab = table.getQuotedName();
        if (!mainSchema) {
            tab = schema.quotedName + '.' + tab;
        }
        tab = escapeIdentifier(tab);
        String[] a = indentation.split(",");
        session.addNode(treeIndex, Integer.parseInt(a[1].trim()), Integer.parseInt(a[2].trim()),
                isView ? "view" : "table", table.getName(), tab);
        treeIndex++;
        if (showColumns) {
            StringBuilder columnsBuilder = new StringBuilder();
            treeIndex = addColumns(mainSchema, table, treeIndex, notManyTables, columnsBuilder);
            if (isView) {
                if (prep != null) {
                    prep.setString(2, table.getName());
                    try (ResultSet rs = prep.executeQuery()) {
                        if (rs.next()) {
                            String sql = rs.getString(1);
                            if (sql != null) {
                                a = indentNode.split(",");
                                session.addNode(treeIndex, Integer.parseInt(a[1].trim()), Integer.parseInt(a[2].trim()),
                                        "type", sql);
                                treeIndex++;
                            }
                        }
                    }
                }
            } else if (!isOracle && notManyTables) {
                treeIndex = addIndexes(mainSchema, meta, table.getName(), schema.name, treeIndex);
            }
            session.addTable(table.getName(), columnsBuilder.toString(), tableId);
        }
        return treeIndex;
    }

    private int addColumns(boolean mainSchema, DbTableOrView table, int treeIndex, boolean showColumnTypes,
            StringBuilder columnsBuilder) {
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
            treeIndex++;
            if (mainSchema && showColumnTypes) {
                session.addNode(treeIndex, 2, 2, "type", column.getDataType());
                treeIndex++;
            }
        }
        return treeIndex;
    }

    /**
     * This class represents index information for the GUI.
     */
    private static class IndexInfo {

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

    private int addIndexes(boolean mainSchema, DatabaseMetaData meta, String table, String schema, int treeIndex)
            throws SQLException {
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
                    type = " (" + session.i18n("text.tree.hashed") + ")";
                } else if (t == DatabaseMetaData.tableIndexOther) {
                    type = "";
                } else {
                    type = null;
                }
                if (name != null && type != null) {
                    info = new IndexInfo();
                    info.name = name;
                    type = (rs.getBoolean("NON_UNIQUE") ? session.i18n("text.tree.nonUnique")
                            : session.i18n("text.tree.unique")) + type;
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
                    session.i18n("text.tree.indexes"));
            treeIndex++;
            String[] a1 = levelIndex.split(",");
            String[] a2 = levelColumnType.split(",");
            for (IndexInfo info : indexMap.values()) {
                session.addNode(treeIndex, Integer.parseInt(a1[1].trim()), Integer.parseInt(a1[2].trim()), "index",
                        info.name);
                treeIndex++;
                session.addNode(treeIndex, Integer.parseInt(a2[1].trim()), Integer.parseInt(a2[2].trim()), "type",
                        info.type);
                treeIndex++;
                session.addNode(treeIndex, Integer.parseInt(a2[1].trim()), Integer.parseInt(a2[2].trim()), "type",
                        info.columns);
                treeIndex++;
            }
        }
        return treeIndex;
    }

    private static String escapeIdentifier(String name) {
        return StringUtils.urlEncode(escapeJavaScript(name)).replace('+', ' ');
    }

    /**
     * Escape text as a the javascript string.
     *
     * @param s the text
     * @return the javascript string
     */
    private static String escapeJavaScript(String s) {
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
