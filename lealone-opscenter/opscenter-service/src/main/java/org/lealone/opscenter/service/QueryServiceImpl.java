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

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.h2.bnf.context.DbContents;
import org.h2.command.Parser;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.security.SHA256;
import org.h2.server.web.PageParser;
import org.h2.tools.SimpleResultSet;
import org.h2.util.JdbcUtils;
import org.h2.util.Profiler;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.lealone.opscenter.service.generated.QueryService;
import org.lealone.orm.json.JsonObject;

public class QueryServiceImpl implements QueryService {

    ServiceSession session;

    private Profiler profiler;

    /**
     * Whether to close the connection.
     */
    private boolean stop;

    private int getMaxrows() {
        String r = (String) session.get("maxrows");
        return r == null ? 0 : Integer.parseInt(r);
    }

    /**
     * Execute a query and append the result to the buffer.
     *
     * @param conn the connection
     * @param s the statement
     * @param i the index
     * @param size the number of statements
     * @param buff the target buffer
     */
    private void query(Connection conn, String s, int i, int size, StringBuilder buff) {
        if (!(s.startsWith("@") && s.endsWith("."))) {
            buff.append(PageParser.escapeHtml(s + ";")).append("<br />");
        }
        boolean forceEdit = s.startsWith("@edit");
        buff.append(getResult(conn, i + 1, s, size == 1, forceEdit)).append("<br />");
    }

    @Override
    public String query(String sql) {
        try {
            ScriptReader r = new ScriptReader(new StringReader(sql));
            final ArrayList<String> list = new ArrayList<>();
            while (true) {
                String s = r.readStatement();
                if (s == null) {
                    break;
                }
                list.add(s);
            }
            final Connection conn = session.getConnection();
            if (SysProperties.CONSOLE_STREAM && ServiceConfig.instance.getAllowChunked()) {
                String page = new String(ServiceConfig.instance.getFile("result.jsp"), StandardCharsets.UTF_8);
                int idx = page.indexOf("${result}");
                // the first element of the list is the header, the last the
                // footer
                list.add(0, page.substring(0, idx));
                list.add(page.substring(idx + "${result}".length()));
                session.put("chunks", new Iterator<String>() {
                    private int i;

                    @Override
                    public boolean hasNext() {
                        return i < list.size();
                    }

                    @Override
                    public String next() {
                        String s = list.get(i++);
                        if (i == 1 || i == list.size()) {
                            return s;
                        }
                        StringBuilder b = new StringBuilder();
                        query(conn, s, i - 1, list.size() - 2, b);
                        return b.toString();
                    }
                });
                return "result.jsp";
            }
            String result;
            StringBuilder buff = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                query(conn, s, i, list.size(), buff);
            }
            result = buff.toString();
            session.put("result", result);
        } catch (Throwable e) {
            session.put("result", getStackTrace(0, e, session.getContents().isH2()));
        }
        return "result.jsp";
    }

    private String getResult(Connection conn, int id, String sql, boolean allowEdit, boolean forceEdit) {
        try {
            sql = sql.trim();
            StringBuilder buff = new StringBuilder();
            String sqlUpper = StringUtils.toUpperEnglish(sql);
            if (sqlUpper.contains("CREATE") || sqlUpper.contains("DROP") || sqlUpper.contains("ALTER")
                    || sqlUpper.contains("RUNSCRIPT")) {
                // String sessionId = attributes.getProperty("jsessionid");
                // buff.append("<script type=\"text/javascript\">parent['h2menu'].location='tables.do?jsessionid=")
                // .append(sessionId).append("';</script>");
                //
                buff.append("<script type=\"text/javascript\">parent['h2menu'].location='tables.do?jsessionid=")
                        .append("").append("';</script>");
            }
            Statement stat;
            DbContents contents = session.getContents();
            if (forceEdit || (allowEdit && contents.isH2())) {
                stat = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            } else {
                stat = conn.createStatement();
            }
            ResultSet rs;
            long time = System.currentTimeMillis();
            boolean metadata = false;
            Object generatedKeys = null;
            boolean edit = false;
            boolean list = false;
            if (JdbcUtils.isBuiltIn(sql, "@autocommit_true")) {
                conn.setAutoCommit(true);
                return "${text.result.autoCommitOn}";
            } else if (JdbcUtils.isBuiltIn(sql, "@autocommit_false")) {
                conn.setAutoCommit(false);
                return "${text.result.autoCommitOff}";
            } else if (JdbcUtils.isBuiltIn(sql, "@cancel")) {
                stat = session.executingStatement;
                if (stat != null) {
                    stat.cancel();
                    buff.append("${text.result.statementWasCanceled}");
                } else {
                    buff.append("${text.result.noRunningStatement}");
                }
                return buff.toString();
            } else if (JdbcUtils.isBuiltIn(sql, "@edit")) {
                edit = true;
                sql = StringUtils.trimSubstring(sql, "@edit".length());
                session.put("resultSetSQL", sql);
            }
            if (JdbcUtils.isBuiltIn(sql, "@list")) {
                list = true;
                sql = StringUtils.trimSubstring(sql, "@list".length());
            }
            if (JdbcUtils.isBuiltIn(sql, "@meta")) {
                metadata = true;
                sql = StringUtils.trimSubstring(sql, "@meta".length());
            }
            if (JdbcUtils.isBuiltIn(sql, "@generated")) {
                generatedKeys = true;
                int offset = "@generated".length();
                int length = sql.length();
                for (; offset < length; offset++) {
                    char c = sql.charAt(offset);
                    if (c == '(') {
                        Parser p = new Parser();
                        generatedKeys = p.parseColumnList(sql, offset);
                        offset = p.getLastParseIndex();
                        break;
                    }
                    if (!Character.isWhitespace(c)) {
                        break;
                    }
                }
                sql = StringUtils.trimSubstring(sql, offset);
            } else if (JdbcUtils.isBuiltIn(sql, "@history")) {
                buff.append(getCommandHistoryString());
                return buff.toString();
            } else if (JdbcUtils.isBuiltIn(sql, "@loop")) {
                sql = StringUtils.trimSubstring(sql, "@loop".length());
                int idx = sql.indexOf(' ');
                int count = Integer.decode(sql.substring(0, idx));
                sql = StringUtils.trimSubstring(sql, idx);
                return executeLoop(conn, count, sql);
            } else if (JdbcUtils.isBuiltIn(sql, "@maxrows")) {
                int maxrows = (int) Double.parseDouble(StringUtils.trimSubstring(sql, "@maxrows".length()));
                session.put("maxrows", Integer.toString(maxrows));
                return "${text.result.maxrowsSet}";
            } else if (JdbcUtils.isBuiltIn(sql, "@parameter_meta")) {
                sql = StringUtils.trimSubstring(sql, "@parameter_meta".length());
                PreparedStatement prep = conn.prepareStatement(sql);
                buff.append(getParameterResultSet(prep.getParameterMetaData()));
                return buff.toString();
            } else if (JdbcUtils.isBuiltIn(sql, "@password_hash")) {
                sql = StringUtils.trimSubstring(sql, "@password_hash".length());
                String[] p = JdbcUtils.split(sql);
                return StringUtils.convertBytesToHex(SHA256.getKeyPasswordHash(p[0], p[1].toCharArray()));
            } else if (JdbcUtils.isBuiltIn(sql, "@prof_start")) {
                if (profiler != null) {
                    profiler.stopCollecting();
                }
                profiler = new Profiler();
                profiler.startCollecting();
                return "Ok";
            } else if (JdbcUtils.isBuiltIn(sql, "@sleep")) {
                String s = StringUtils.trimSubstring(sql, "@sleep".length());
                int sleep = 1;
                if (s.length() > 0) {
                    sleep = Integer.parseInt(s);
                }
                Thread.sleep(sleep * 1000);
                return "Ok";
            } else if (JdbcUtils.isBuiltIn(sql, "@transaction_isolation")) {
                String s = StringUtils.trimSubstring(sql, "@transaction_isolation".length());
                if (s.length() > 0) {
                    int level = Integer.parseInt(s);
                    conn.setTransactionIsolation(level);
                }
                buff.append("Transaction Isolation: ").append(conn.getTransactionIsolation()).append("<br />");
                buff.append(Connection.TRANSACTION_READ_UNCOMMITTED).append(": read_uncommitted<br />");
                buff.append(Connection.TRANSACTION_READ_COMMITTED).append(": read_committed<br />");
                buff.append(Connection.TRANSACTION_REPEATABLE_READ).append(": repeatable_read<br />");
                buff.append(Constants.TRANSACTION_SNAPSHOT).append(": snapshot<br />");
                buff.append(Connection.TRANSACTION_SERIALIZABLE).append(": serializable");
            }
            if (sql.startsWith("@")) {
                rs = JdbcUtils.getMetaResultSet(conn, sql);
                if (rs == null && JdbcUtils.isBuiltIn(sql, "@prof_stop")) {
                    if (profiler != null) {
                        profiler.stopCollecting();
                        SimpleResultSet simple = new SimpleResultSet();
                        simple.addColumn("Top Stack Trace(s)", Types.VARCHAR, 0, 0);
                        simple.addRow(profiler.getTop(3));
                        rs = simple;
                        profiler = null;
                    }
                }
                if (rs == null) {
                    buff.append("?: ").append(sql);
                    return buff.toString();
                }
            } else {
                int maxrows = getMaxrows();
                stat.setMaxRows(maxrows);
                session.executingStatement = stat;
                boolean isResultSet;
                if (generatedKeys == null) {
                    isResultSet = stat.execute(sql);
                } else if (generatedKeys instanceof Boolean) {
                    isResultSet = stat.execute(sql,
                            ((Boolean) generatedKeys) ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
                } else if (generatedKeys instanceof String[]) {
                    isResultSet = stat.execute(sql, (String[]) generatedKeys);
                } else {
                    isResultSet = stat.execute(sql, (int[]) generatedKeys);
                }
                session.addCommand(sql);
                if (generatedKeys != null) {
                    rs = null;
                    rs = stat.getGeneratedKeys();
                } else {
                    if (!isResultSet) {
                        long updateCount;
                        try {
                            updateCount = stat.getLargeUpdateCount();
                        } catch (UnsupportedOperationException e) {
                            updateCount = stat.getUpdateCount();
                        }
                        buff.append("${text.result.updateCount}: ").append(updateCount);
                        time = System.currentTimeMillis() - time;
                        buff.append("<br />(").append(time).append(" ms)");
                        stat.close();
                        return buff.toString();
                    }
                    rs = stat.getResultSet();
                }
            }
            time = System.currentTimeMillis() - time;
            buff.append(getResultSet(sql, rs, metadata, list, edit, time, allowEdit));
            // SQLWarning warning = stat.getWarnings();
            // if (warning != null) {
            // buff.append("<br />Warning:<br />").
            // append(getStackTrace(id, warning));
            // }
            if (!edit) {
                stat.close();
            }
            return buff.toString();
        } catch (Throwable e) {
            // throwable: including OutOfMemoryError and so on
            return getStackTrace(id, e, session.getContents().isH2());
        } finally {
            session.executingStatement = null;
        }
    }

    private String getResultSet(String sql, ResultSet rs, boolean metadata, boolean list, boolean edit, long time,
            boolean allowEdit) throws SQLException {
        int maxrows = getMaxrows();
        time = System.currentTimeMillis() - time;
        StringBuilder buff = new StringBuilder();
        if (edit) {
            buff.append("<form id=\"editing\" name=\"editing\" method=\"post\" "
                    + "action=\"editResult.do?jsessionid=${sessionId}\" " + "id=\"mainForm\" target=\"h2result\">"
                    + "<input type=\"hidden\" name=\"op\" value=\"1\" />"
                    + "<input type=\"hidden\" name=\"row\" value=\"\" />"
                    + "<table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\" id=\"editTable\">");
        } else {
            buff.append("<table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\">");
        }
        if (metadata) {
            SimpleResultSet r = new SimpleResultSet();
            r.addColumn("#", Types.INTEGER, 0, 0);
            r.addColumn("label", Types.VARCHAR, 0, 0);
            r.addColumn("catalog", Types.VARCHAR, 0, 0);
            r.addColumn("schema", Types.VARCHAR, 0, 0);
            r.addColumn("table", Types.VARCHAR, 0, 0);
            r.addColumn("column", Types.VARCHAR, 0, 0);
            r.addColumn("type", Types.INTEGER, 0, 0);
            r.addColumn("typeName", Types.VARCHAR, 0, 0);
            r.addColumn("class", Types.VARCHAR, 0, 0);
            r.addColumn("precision", Types.INTEGER, 0, 0);
            r.addColumn("scale", Types.INTEGER, 0, 0);
            r.addColumn("displaySize", Types.INTEGER, 0, 0);
            r.addColumn("autoIncrement", Types.BOOLEAN, 0, 0);
            r.addColumn("caseSensitive", Types.BOOLEAN, 0, 0);
            r.addColumn("currency", Types.BOOLEAN, 0, 0);
            r.addColumn("nullable", Types.INTEGER, 0, 0);
            r.addColumn("readOnly", Types.BOOLEAN, 0, 0);
            r.addColumn("searchable", Types.BOOLEAN, 0, 0);
            r.addColumn("signed", Types.BOOLEAN, 0, 0);
            r.addColumn("writable", Types.BOOLEAN, 0, 0);
            r.addColumn("definitelyWritable", Types.BOOLEAN, 0, 0);
            ResultSetMetaData m = rs.getMetaData();
            for (int i = 1; i <= m.getColumnCount(); i++) {
                r.addRow(i, m.getColumnLabel(i), m.getCatalogName(i), m.getSchemaName(i), m.getTableName(i),
                        m.getColumnName(i), m.getColumnType(i), m.getColumnTypeName(i), m.getColumnClassName(i),
                        m.getPrecision(i), m.getScale(i), m.getColumnDisplaySize(i), m.isAutoIncrement(i),
                        m.isCaseSensitive(i), m.isCurrency(i), m.isNullable(i), m.isReadOnly(i), m.isSearchable(i),
                        m.isSigned(i), m.isWritable(i), m.isDefinitelyWritable(i));
            }
            rs = r;
        }
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();
        int rows = 0;
        if (list) {
            buff.append("<tr><th>Column</th><th>Data</th></tr><tr>");
            while (rs.next()) {
                if (maxrows > 0 && rows >= maxrows) {
                    break;
                }
                rows++;
                buff.append("<tr><td>Row #</td><td>").append(rows).append("</tr>");
                for (int i = 0; i < columns; i++) {
                    buff.append("<tr><td>").append(PageParser.escapeHtml(meta.getColumnLabel(i + 1)))
                            .append("</td><td>").append(escapeData(rs, i + 1)).append("</td></tr>");
                }
            }
        } else {
            buff.append("<tr>");
            if (edit) {
                buff.append("<th>${text.resultEdit.action}</th>");
            }
            for (int i = 0; i < columns; i++) {
                buff.append("<th>").append(PageParser.escapeHtml(meta.getColumnLabel(i + 1))).append("</th>");
            }
            buff.append("</tr>");
            while (rs.next()) {
                if (maxrows > 0 && rows >= maxrows) {
                    break;
                }
                rows++;
                buff.append("<tr>");
                if (edit) {
                    buff.append("<td>").append("<img onclick=\"javascript:editRow(").append(rs.getRow())
                            .append(",'${sessionId}', '${text.resultEdit.save}', " + "'${text.resultEdit.cancel}'")
                            .append(")\" width=16 height=16 src=\"ico_write.gif\" "
                                    + "onmouseover = \"this.className ='icon_hover'\" "
                                    + "onmouseout = \"this.className ='icon'\" "
                                    + "class=\"icon\" alt=\"${text.resultEdit.edit}\" "
                                    + "title=\"${text.resultEdit.edit}\" border=\"1\"/>")
                            .append("<img onclick=\"javascript:deleteRow(").append(rs.getRow())
                            .append(",'${sessionId}', '${text.resultEdit.delete}', " + "'${text.resultEdit.cancel}'")
                            .append(")\" width=16 height=16 src=\"ico_remove.gif\" "
                                    + "onmouseover = \"this.className ='icon_hover'\" "
                                    + "onmouseout = \"this.className ='icon'\" "
                                    + "class=\"icon\" alt=\"${text.resultEdit.delete}\" "
                                    + "title=\"${text.resultEdit.delete}\" border=\"1\" /></a>")
                            .append("</td>");
                }
                for (int i = 0; i < columns; i++) {
                    buff.append("<td>").append(escapeData(rs, i + 1)).append("</td>");
                }
                buff.append("</tr>");
            }
        }
        boolean isUpdatable = false;
        try {
            if (!session.getContents().isDB2()) {
                isUpdatable = rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE
                        && rs.getType() != ResultSet.TYPE_FORWARD_ONLY;
            }
        } catch (NullPointerException e) {
            // ignore
            // workaround for a JDBC-ODBC bridge problem
        }
        if (edit) {
            ResultSet old = session.result;
            if (old != null) {
                old.close();
            }
            session.result = rs;
        } else {
            rs.close();
        }
        if (edit) {
            buff.append("<tr><td>")
                    .append("<img onclick=\"javascript:editRow(-1, "
                            + "'${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'")
                    .append(")\" width=16 height=16 src=\"ico_add.gif\" "
                            + "onmouseover = \"this.className ='icon_hover'\" "
                            + "onmouseout = \"this.className ='icon'\" "
                            + "class=\"icon\" alt=\"${text.resultEdit.add}\" "
                            + "title=\"${text.resultEdit.add}\" border=\"1\"/>")
                    .append("</td>");
            for (int i = 0; i < columns; i++) {
                buff.append("<td></td>");
            }
            buff.append("</tr>");
        }
        buff.append("</table>");
        if (edit) {
            buff.append("</form>");
        }
        if (rows == 0) {
            buff.append("(${text.result.noRows}");
        } else if (rows == 1) {
            buff.append("(${text.result.1row}");
        } else {
            buff.append('(').append(rows).append(" ${text.result.rows}");
        }
        buff.append(", ");
        time = System.currentTimeMillis() - time;
        buff.append(time).append(" ms)");
        if (!edit && isUpdatable && allowEdit) {
            buff.append("<br /><br />" + "<form name=\"editResult\" method=\"post\" "
                    + "action=\"query.do?jsessionid=${sessionId}\" target=\"h2result\">"
                    + "<input type=\"submit\" class=\"button\" " + "value=\"${text.resultEdit.editResult}\" />"
                    + "<input type=\"hidden\" name=\"sql\" value=\"@edit ").append(sql).append("\" /></form>");
        }
        return buff.toString();
    }

    private String executeLoop(Connection conn, int count, String sql) throws SQLException {
        ArrayList<Integer> params = new ArrayList<>();
        int idx = 0;
        while (!stop) {
            idx = sql.indexOf('?', idx);
            if (idx < 0) {
                break;
            }
            if (JdbcUtils.isBuiltIn(sql.substring(idx), "?/*rnd*/")) {
                params.add(1);
                sql = sql.substring(0, idx) + "?" + sql.substring(idx + "/*rnd*/".length() + 1);
            } else {
                params.add(0);
            }
            idx++;
        }
        boolean prepared;
        Random random = new Random(1);
        long time = System.currentTimeMillis();
        if (JdbcUtils.isBuiltIn(sql, "@statement")) {
            sql = StringUtils.trimSubstring(sql, "@statement".length());
            prepared = false;
            Statement stat = conn.createStatement();
            for (int i = 0; !stop && i < count; i++) {
                String s = sql;
                for (Integer type : params) {
                    idx = s.indexOf('?');
                    if (type == 1) {
                        s = s.substring(0, idx) + random.nextInt(count) + s.substring(idx + 1);
                    } else {
                        s = s.substring(0, idx) + i + s.substring(idx + 1);
                    }
                }
                if (stat.execute(s)) {
                    ResultSet rs = stat.getResultSet();
                    while (!stop && rs.next()) {
                        // maybe get the data as well
                    }
                    rs.close();
                }
            }
        } else {
            prepared = true;
            PreparedStatement prep = conn.prepareStatement(sql);
            for (int i = 0; !stop && i < count; i++) {
                for (int j = 0; j < params.size(); j++) {
                    Integer type = params.get(j);
                    if (type == 1) {
                        prep.setInt(j + 1, random.nextInt(count));
                    } else {
                        prep.setInt(j + 1, i);
                    }
                }
                if (session.getContents().isSQLite()) {
                    // SQLite currently throws an exception on prep.execute()
                    prep.executeUpdate();
                } else {
                    if (prep.execute()) {
                        ResultSet rs = prep.getResultSet();
                        while (!stop && rs.next()) {
                            // maybe get the data as well
                        }
                        rs.close();
                    }
                }
            }
        }
        time = System.currentTimeMillis() - time;
        StringBuilder builder = new StringBuilder().append(time).append(" ms: ").append(count).append(" * ")
                .append(prepared ? "(Prepared) " : "(Statement) ").append('(');
        for (int i = 0, size = params.size(); i < size; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(params.get(i) == 0 ? "i" : "rnd");
        }
        return builder.append(") ").append(sql).toString();
    }

    private String getCommandHistoryString() {
        StringBuilder buff = new StringBuilder();
        ArrayList<String> history = session.getCommandHistory();
        buff.append("<table cellspacing=0 cellpadding=0>" + "<tr><th></th><th>Command</th></tr>");
        for (int i = history.size() - 1; i >= 0; i--) {
            String sql = history.get(i);
            buff.append("<tr><td><a href=\"getHistory.do?id=").append(i)
                    .append("&jsessionid=${sessionId}\" target=\"h2query\" >")
                    .append("<img width=16 height=16 src=\"ico_write.gif\" "
                            + "onmouseover = \"this.className ='icon_hover'\" ")
                    .append("onmouseout = \"this.className ='icon'\" "
                            + "class=\"icon\" alt=\"${text.resultEdit.edit}\" ")
                    .append("title=\"${text.resultEdit.edit}\" border=\"1\"/></a>").append("</td><td>")
                    .append(PageParser.escapeHtml(sql)).append("</td></tr>");
        }
        buff.append("</table>");
        return buff.toString();
    }

    private static String getParameterResultSet(ParameterMetaData meta) throws SQLException {
        StringBuilder buff = new StringBuilder();
        if (meta == null) {
            return "No parameter meta data";
        }
        buff.append("<table cellspacing=0 cellpadding=0>").append("<tr><th>className</th><th>mode</th><th>type</th>")
                .append("<th>typeName</th><th>precision</th><th>scale</th></tr>");
        for (int i = 0; i < meta.getParameterCount(); i++) {
            buff.append("</tr><td>").append(meta.getParameterClassName(i + 1)).append("</td><td>")
                    .append(meta.getParameterMode(i + 1)).append("</td><td>").append(meta.getParameterType(i + 1))
                    .append("</td><td>").append(meta.getParameterTypeName(i + 1)).append("</td><td>")
                    .append(meta.getPrecision(i + 1)).append("</td><td>").append(meta.getScale(i + 1))
                    .append("</td></tr>");
        }
        buff.append("</table>");
        return buff.toString();
    }

    private static String escapeData(ResultSet rs, int columnIndex) throws SQLException {
        if (DataType.isBinaryColumn(rs.getMetaData(), columnIndex)) {
            byte[] d = rs.getBytes(columnIndex);
            if (d == null) {
                return "<i>null</i>";
            } else if (d.length > 50_000) {
                return "<div style='display: none'>=+</div>" + StringUtils.convertBytesToHex(d, 3) + "... (" + d.length
                        + " ${text.result.bytes})";
            }
            return StringUtils.convertBytesToHex(d);
        }
        String d = rs.getString(columnIndex);
        if (d == null) {
            return "<i>null</i>";
        } else if (d.length() > 100_000) {
            return "<div style='display: none'>=+</div>" + PageParser.escapeHtml(d.substring(0, 100)) + "... ("
                    + d.length() + " ${text.result.characters})";
        } else if (d.equals("null") || d.startsWith("= ") || d.startsWith("=+")) {
            return "<div style='display: none'>= </div>" + PageParser.escapeHtml(d);
        } else if (d.equals("")) {
            // PageParser.escapeHtml replaces "" with a non-breaking space
            return "";
        }
        return PageParser.escapeHtml(d);
    }

    @Override
    public String editResult(Integer row, Integer op, String value) {
        JsonObject attributes = new JsonObject(value);
        ResultSet rs = session.result;
        String result = "", error = "";
        try {
            if (op == 1) {
                boolean insert = row < 0;
                if (insert) {
                    rs.moveToInsertRow();
                } else {
                    rs.absolute(row);
                }
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    String x = attributes.getString("r" + row + "c" + (i + 1));
                    unescapeData(x, rs, i + 1);
                }
                if (insert) {
                    rs.insertRow();
                } else {
                    rs.updateRow();
                }
            } else if (op == 2) {
                rs.absolute(row);
                rs.deleteRow();
            } else if (op == 3) {
                // cancel
            }
        } catch (Throwable e) {
            result = "<br />" + getStackTrace(0, e, session.getContents().isH2());
            error = formatAsError(e.getMessage());
        }
        String sql = "@edit " + (String) session.get("resultSetSQL");
        Connection conn = session.getConnection();
        result = error + getResult(conn, -1, sql, true, true) + result;
        session.put("result", result);
        return "result.jsp";
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
            ServiceConfig.instance.traceError(e);
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

    private void unescapeData(String x, ResultSet rs, int columnIndex) throws SQLException {
        if (x.equals("null")) {
            rs.updateNull(columnIndex);
            return;
        } else if (x.startsWith("=+")) {
            // don't update
            return;
        } else if (x.equals("=*")) {
            // set an appropriate default value
            int type = rs.getMetaData().getColumnType(columnIndex);
            switch (type) {
            case Types.TIME:
                rs.updateString(columnIndex, "12:00:00");
                break;
            case Types.TIMESTAMP:
            case Types.DATE:
                rs.updateString(columnIndex, "2001-01-01");
                break;
            default:
                rs.updateString(columnIndex, "1");
                break;
            }
            return;
        } else if (x.startsWith("= ")) {
            x = x.substring(2);
        }
        ResultSetMetaData meta = rs.getMetaData();
        if (DataType.isBinaryColumn(meta, columnIndex)) {
            rs.updateBytes(columnIndex, StringUtils.convertHexToBytes(x));
            return;
        }
        int type = meta.getColumnType(columnIndex);
        if (session.getContents().isH2()) {
            rs.updateString(columnIndex, x);
            return;
        }
        switch (type) {
        case Types.BIGINT:
            rs.updateLong(columnIndex, Long.decode(x));
            break;
        case Types.DECIMAL:
            rs.updateBigDecimal(columnIndex, new BigDecimal(x));
            break;
        case Types.DOUBLE:
        case Types.FLOAT:
            rs.updateDouble(columnIndex, Double.parseDouble(x));
            break;
        case Types.REAL:
            rs.updateFloat(columnIndex, Float.parseFloat(x));
            break;
        case Types.INTEGER:
            rs.updateInt(columnIndex, Integer.decode(x));
            break;
        case Types.TINYINT:
            rs.updateShort(columnIndex, Short.decode(x));
            break;
        default:
            rs.updateString(columnIndex, x);
        }
    }
}
