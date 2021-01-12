/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.lealone.opscenter.web.deprecated;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.h2.message.DbException;
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

public class WebAdminHandler extends WebHandler {

    WebAdminHandler(WebServer server) {
        super(server);
    }

    String processAdminCommand(String file) {
        while (file.endsWith(".do")) {
            switch (file) {
            case "admin.do":
                file = checkAdmin(file) ? admin() : "adminLogin.do";
                break;
            case "adminSave.do":
                file = checkAdmin(file) ? adminSave() : "adminLogin.do";
                break;
            case "adminStartTranslate.do":
                file = checkAdmin(file) ? adminStartTranslate() : "adminLogin.do";
                break;
            case "adminShutdown.do":
                file = checkAdmin(file) ? adminShutdown() : "adminLogin.do";
                break;
            case "tools.do":
                file = checkAdmin(file) ? tools() : "adminLogin.do";
                break;
            case "adminLogin.do":
                file = adminLogin();
                break;
            default:
                file = "error.jsp";
                break;
            }
        }
        return file;
    }

    private String adminLogin() {
        String password = attributes.getProperty("password");
        if (password == null || password.isEmpty() || !server.checkAdminPassword(password)) {
            return "adminLogin.jsp";
        }
        String back = (String) session.remove("adminBack");
        session.put("admin", true);
        return back != null ? back : "admin.do";
    }

    private String admin() {
        session.put("port", Integer.toString(server.getPort()));
        session.put("allowOthers", Boolean.toString(server.getAllowOthers()));
        session.put("ssl", String.valueOf(server.getSSL()));
        session.put("sessions", server.getSessions());
        return "admin.jsp";
    }

    private String adminSave() {
        try {
            Properties prop = new SortedProperties();
            int port = Integer.decode((String) attributes.get("port"));
            prop.setProperty("webPort", Integer.toString(port));
            server.setPort(port);
            boolean allowOthers = Utils.parseBoolean((String) attributes.get("allowOthers"), false, false);
            prop.setProperty("webAllowOthers", String.valueOf(allowOthers));
            server.setAllowOthers(allowOthers);
            boolean ssl = Utils.parseBoolean((String) attributes.get("ssl"), false, false);
            prop.setProperty("webSSL", String.valueOf(ssl));
            server.setSSL(ssl);
            byte[] adminPassword = server.getAdminPassword();
            if (adminPassword != null) {
                prop.setProperty("webAdminPassword", StringUtils.convertBytesToHex(adminPassword));
            }
            server.saveProperties(prop);
        } catch (Exception e) {
            trace(e.toString());
        }
        return admin();
    }

    private String tools() {
        try {
            String toolName = (String) attributes.get("tool");
            session.put("tool", toolName);
            String args = (String) attributes.get("args");
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
                session.put("toolResult", getStackTrace(0, e, true));
            }
        } catch (Exception e) {
            server.traceError(e);
        }
        return "tools.jsp";
    }

    private String adminStartTranslate() {
        Map<?, ?> p = Map.class.cast(session.map.get("text"));
        @SuppressWarnings("unchecked")
        Map<Object, Object> p2 = (Map<Object, Object>) p;
        String file = server.startTranslate(p2);
        session.put("translationFile", file);
        return "helpTranslate.jsp";
    }

    /**
     * Stop the application and the server.
     *
     * @return the page to display
     */
    private String adminShutdown() {
        server.shutdown();
        return "admin.jsp";
    }
}
