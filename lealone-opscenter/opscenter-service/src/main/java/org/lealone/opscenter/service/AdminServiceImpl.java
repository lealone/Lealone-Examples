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

import static org.lealone.opscenter.service.ServiceConfig.instance;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.h2.message.DbException;
import org.h2.server.web.PageParser;
import org.h2.tools.Backup;
import org.h2.tools.ChangeFileEncryption;
import org.h2.tools.ConvertTraceFile;
import org.h2.tools.CreateCluster;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Recover;
import org.h2.tools.Restore;
import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.lealone.opscenter.service.generated.AdminService;
import org.lealone.orm.json.JsonArray;
import org.lealone.orm.json.JsonObject;

public class AdminServiceImpl implements AdminService {
    ServiceSession session;

    @Override
    public String login(String password) {
        // if (password == null || password.isEmpty() || !server.checkAdminPassword(password)) {
        // return "adminLogin.jsp";
        // }
        // String back = (String) session.remove("adminBack");
        // session.put("admin", true);
        // return back != null ? back : "admin.do";

        if (password == null || password.isEmpty() || !instance.checkAdminPassword(password)) {
            return "failed";
        }

        return "ok";
    }

    @Override
    public String logout() {
        return "ok";
    }

    @Override
    public String admin() {
        JsonObject json = new JsonObject();
        json.put("port", Integer.toString(instance.getPort()));
        json.put("allowOthers", Boolean.toString(instance.getAllowOthers()));
        json.put("ssl", String.valueOf(instance.getSSL()));
        json.put("sessions", new JsonArray(instance.getSessions()));
        return json.encode();
    }

    @Override
    public String save(String port0, String allowOthers0, String ssl0) {
        try {
            Properties prop = new SortedProperties();
            int port = Integer.decode(port0);
            prop.setProperty("webPort", Integer.toString(port));
            instance.setPort(port);
            boolean allowOthers = Utils.parseBoolean(allowOthers0, false, false);
            prop.setProperty("webAllowOthers", String.valueOf(allowOthers));
            instance.setAllowOthers(allowOthers);
            boolean ssl = Utils.parseBoolean(ssl0, false, false);
            prop.setProperty("webSSL", String.valueOf(ssl));
            instance.setSSL(ssl);
            byte[] adminPassword = instance.getAdminPassword();
            if (adminPassword != null) {
                prop.setProperty("webAdminPassword", StringUtils.convertBytesToHex(adminPassword));
            }
            instance.saveProperties(prop);
        } catch (Exception e) {
            instance.trace(e.toString());
        }
        return admin();
    }

    @Override
    public String tools(String tool0, String args) {
        try {
            String toolName = tool0;
            session.put("tool", toolName);
            String[] argList = StringUtils.arraySplit(args, ',', false);
            Tool tool = null;
            if ("Backup".equals(toolName)) {
                tool = new Backup();
            } else if ("Restore".equals(toolName)) {
                tool = new Restore();
            } else if ("Recover".equals(toolName)) {
                tool = new Recover();
            } else if ("DeleteDbFiles".equals(toolName)) {
                tool = new DeleteDbFiles();
            } else if ("ChangeFileEncryption".equals(toolName)) {
                tool = new ChangeFileEncryption();
            } else if ("Script".equals(toolName)) {
                tool = new Script();
            } else if ("RunScript".equals(toolName)) {
                tool = new RunScript();
            } else if ("ConvertTraceFile".equals(toolName)) {
                tool = new ConvertTraceFile();
            } else if ("CreateCluster".equals(toolName)) {
                tool = new CreateCluster();
            } else {
                throw DbException.getInternalError(toolName);
            }
            ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(outBuff, false, "UTF-8");
            tool.setOut(out);
            try {
                tool.runTool(argList);
                out.flush();
                String o = Utils10.byteArrayOutputStreamToString(outBuff, StandardCharsets.UTF_8);
                String result = PageParser.escapeHtml(o);
                session.put("toolResult", result);
            } catch (Exception e) {
                // session.put("toolResult", getStackTrace(0, e, true));
            }
        } catch (Exception e) {
            instance.traceError(e);
        }
        return "ok";
    }

    @Override
    public String startTranslate() {
        Map<?, ?> p = Map.class.cast(session.map.get("text"));
        @SuppressWarnings("unchecked")
        Map<Object, Object> p2 = (Map<Object, Object>) p;
        String file = instance.startTranslate(p2);
        session.put("translationFile", file);
        JsonObject json = new JsonObject();
        json.put("translationFile", file);
        return json.encode(); // "helpTranslate.jsp";
    }

    /**
     * Stop the application and the server.
     *
     * @return the page to display
     */
    @Override
    public String shutdown() {
        instance.shutdown();
        return "ok";
    }
}
