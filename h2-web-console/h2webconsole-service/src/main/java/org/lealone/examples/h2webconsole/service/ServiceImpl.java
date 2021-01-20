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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import org.h2.engine.Constants;
import org.h2.server.web.PageParser;
import org.h2.util.StringUtils;

public abstract class ServiceImpl {

    ServiceSession session;

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

}
