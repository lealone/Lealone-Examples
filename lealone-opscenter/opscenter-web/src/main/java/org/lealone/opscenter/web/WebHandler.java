/*
 * Copyright 2004-2021 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.lealone.opscenter.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.h2.engine.SysProperties;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.StringUtils;
import org.lealone.common.util.CaseInsensitiveMap;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.impl.SocketAddressImpl;
import io.vertx.ext.web.RoutingContext;

/**
 * For each connection to a session, an object of this class is created.
 * This class is used by the H2 Console.
 */
class WebHandler extends WebApp {

    WebHandler(WebServer server) {
        super(server);
    }

    private String getAllowedFile(String requestedFile, InetAddress address) {
        if (!allow(address)) {
            return "notAllowed.jsp";
        }
        if (requestedFile.length() == 0) {
            return "index.do";
        }
        if (requestedFile.charAt(0) == '?') {
            return "index.do" + requestedFile;
        }
        return requestedFile;
    }

    private boolean allow(InetAddress address) {
        if (server.getAllowOthers()) {
            return true;
        }
        try {
            return isLocalAddress(address);
        } catch (UnknownHostException e) {
            server.traceError(e);
            return false;
        }
    }

    private static boolean isLocalAddress(InetAddress test) throws UnknownHostException {
        if (test.isLoopbackAddress()) {
            return true;
        }
        InetAddress localhost = InetAddress.getLocalHost();
        // localhost.getCanonicalHostName() is very slow
        String host = localhost.getHostAddress();
        for (InetAddress addr : InetAddress.getAllByName(host)) {
            if (test.equals(addr)) {
                return true;
            }
        }
        return false;
    }

    private void parseHeader(RoutingContext routingContext) throws IOException {
        for (Map.Entry<String, String> e : routingContext.request().headers().entries()) {
            String header = StringUtils.toLowerEnglish(e.getKey());
            String value = StringUtils.toLowerEnglish(e.getValue());
            if (header.equals("user-agent")) {
                boolean isWebKit = value.contains("webkit/");
                if (isWebKit && session != null) {
                    // workaround for what seems to be a WebKit bug:
                    // https://bugs.chromium.org/p/chromium/issues/detail?id=6402
                    session.put("frame-border", "1");
                    session.put("frameset-border", "2");
                }
            } else if (header.equals("accept-language")) {
                Locale locale = session == null ? null : session.locale;
                if (locale == null) {
                    String languages = value;
                    StringTokenizer tokenizer = new StringTokenizer(languages, ",;");
                    while (tokenizer.hasMoreTokens()) {
                        String token = tokenizer.nextToken();
                        if (!token.startsWith("q=")) {
                            if (server.supportsLanguage(token)) {
                                int dash = token.indexOf('-');
                                if (dash >= 0) {
                                    String language = token.substring(0, dash);
                                    String country = token.substring(dash + 1);
                                    locale = new Locale(language, country);
                                } else {
                                    locale = new Locale(token, "");
                                }
                                headerLanguage = locale.getLanguage();
                                if (session != null) {
                                    session.locale = locale;
                                    session.put("language", headerLanguage);
                                    server.readTranslations(session, headerLanguage);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void trace(String s) {
        server.trace(s);
    }

    @SuppressWarnings("unchecked")
    void process(RoutingContext routingContext, CaseInsensitiveMap<Object> params) throws IOException {
        parseHeader(routingContext);
        InetAddress localAddress = ((SocketAddressImpl) routingContext.request().localAddress()).ipAddress();
        InetAddress remoteAddress = ((SocketAddressImpl) routingContext.request().remoteAddress()).ipAddress();
        String file = routingContext.request().path();
        if (file.startsWith("/"))
            file = file.substring(1);
        trace("file: " + file);
        file = getAllowedFile(file, localAddress);
        attributes = new Properties();
        for (Map.Entry<String, Object> e : params.entrySet()) {
            attributes.put(e.getKey().toLowerCase(), e.getValue());
        }
        String key = attributes.getProperty("key");
        String sessionId = attributes.getProperty("jsessionid");
        session = server.getSession(sessionId);

        NetworkConnectionInfo connInfo = new NetworkConnectionInfo(
                NetUtils.ipToShortForm(new StringBuilder(server.getSSL() ? "https://" : "http://"),
                        localAddress.getAddress(), true) //
                        .append(':').append(routingContext.request().localAddress().port()).toString(), //
                remoteAddress.getAddress(), routingContext.request().remoteAddress().port(), null);

        file = processRequest(file, connInfo);
        if (file.length() == 0) {
            // asynchronous request
            return;
        }
        byte[] bytes = server.getFile(file);
        if (bytes == null) {
            routingContext.fail(404);
            return;
        }

        routingContext.response().setStatusCode(200);
        routingContext.response().putHeader("Content-Type", mimeType);
        routingContext.response().putHeader("Cache-Control", "no-cache");
        if (session != null && file.endsWith(".jsp")) {
            if (key != null) {
                session.put("key", key);
            }
            String page = new String(bytes, StandardCharsets.UTF_8);
            if (SysProperties.CONSOLE_STREAM) {
                Iterator<String> it = (Iterator<String>) session.map.remove("chunks");
                if (it != null) {
                    routingContext.response().putHeader("Transfer-Encoding", "chunked");
                    while (it.hasNext()) {
                        String s = it.next();
                        s = PageParser.parse(s, session.map);
                        bytes = s.getBytes(StandardCharsets.UTF_8);
                        if (bytes.length == 0) {
                            continue;
                        }
                        write(routingContext, bytes);
                    }
                    routingContext.response().end();
                    return;
                }
            }
            page = PageParser.parse(page, session.map);
            bytes = page.getBytes(StandardCharsets.UTF_8);
        }
        routingContext.response().putHeader("Content-Length", bytes.length + "");
        write(routingContext, bytes);
        routingContext.response().end();
        return;
    }

    private static void write(RoutingContext routingContext, byte[] message) {
        routingContext.response().write(Buffer.buffer(message));
    }
}
