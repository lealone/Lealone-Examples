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
package org.lealone.examples.h2webconsole.web;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.lealone.examples.h2webconsole.service.ServiceConfig;
import org.lealone.examples.h2webconsole.web.deprecated.WebOpsHandler;
import org.lealone.examples.h2webconsole.web.deprecated.WebServer;
import org.lealone.examples.h2webconsole.web.thymeleaf.ThymeleafTemplateEngineImpl;
import org.lealone.server.http.HttpRouterFactory;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;

public class WebConsoleRouterFactory extends HttpRouterFactory {

    private final WebServer webServer = new WebServer();

    @Override
    protected void initRouter(Map<String, String> config, Vertx vertx, Router router) {
        int size = config.size() * 2 + 3;
        String[] args = new String[size];
        int index = 0;
        for (Map.Entry<String, String> e : config.entrySet()) {
            args[index++] = e.getKey();
            args[index++] = e.getValue();
        }
        // 临时测试用
        args[index++] = "-ifNotExists";
        args[index++] = "-webAdminPassword";
        args[index++] = "root";
        webServer.init(args);
        ServiceConfig.instance.init(args);

        // route的顺序很重要，所以下面三个调用不能乱
        setSessionHandler(vertx, router);
        setRedirectHandler(router);
        setDevelopmentEnvironmentRouter(config, vertx, router);
        setH2Handler(vertx, router);
    }

    private void setSessionHandler(Vertx vertx, Router router) {
        router.route().handler(SessionHandler.create(SessionStore.create(vertx)));
        router.route().handler(BodyHandler.create(false));
        // router.route("/service/admin_service/logout").handler(routingContext -> {
        // routingContext.session().remove("jsessionid");
        // routingContext.next();
        // });
    }

    private void setRedirectHandler(Router router) {
        router.route("/").handler(routingContext -> {
            routingContext.redirect("/admin/index.html");
        });
        router.route("/admin").handler(routingContext -> {
            routingContext.redirect("/admin/index.html");
        });
        router.route("/ops").handler(routingContext -> {
            routingContext.redirect("/ops/index.html");
        });
        router.route("/old").handler(routingContext -> {
            routingContext.redirect("/index.do");
        });
    }

    private void setH2Handler(Vertx vertx, Router router) {
        // 用正则表达式判断路径是否以“.do”结尾（不区分大小写）
        setH2Handler(router, ".*\\.(?i)do");
        setH2Handler(router, ".*\\.(?i)jsp");

        router.route().handler(routingContext -> {
            String file = routingContext.request().path();
            if (file.startsWith("/ops/") || file.startsWith("/common/") || file.startsWith("/admin/")
                    || file.startsWith("/service/") || file.startsWith("/_lealone_sockjs_/")
                    || file.startsWith("/deprecated/"))
                routingContext.next();
            else if (file.endsWith(".css"))
                routingContext.redirect("/common/css/opscenter.css");
            else if (file.equals("/tree.js"))
                routingContext.redirect("/deprecated/js/tree.js");
            else if (file.equals("/table.js"))
                routingContext.redirect("/deprecated/js/table.js");
            else if (file.endsWith(".js"))
                routingContext.redirect("/ops/js" + file);
            else
                routingContext.redirect("/ops/img" + file);
        });
    }

    private void setH2Handler(Router router, String regex) {
        router.routeWithRegex(regex).handler(routingContext -> {
            try {
                new WebOpsHandler(webServer).process(routingContext, getMethodArgs(routingContext, false));
            } catch (IOException e) {
                routingContext.failed();
            }
        });
    }

    @Override
    protected void sendHttpServiceResponse(RoutingContext routingContext, String serviceName, String methodName,
            Buffer result) {
        // if ("ops_service".equalsIgnoreCase(serviceName) && "login".equalsIgnoreCase(methodName)) {
        // routingContext.session().put("jsessionid", result.toString());
        // }
        super.sendHttpServiceResponse(routingContext, serviceName, methodName, result);
    }

    private void setDevelopmentEnvironmentRouter(Map<String, String> config, Vertx vertx, Router router) {
        if (!isDevelopmentEnvironment(config))
            return;

        System.setProperty("vertxweb.environment", "development");
        router.routeWithRegex(".*/fragment/.*").handler(routingContext -> {
            routingContext.fail(404);// 不允许访问Thymeleaf的fragment文件
        });

        String webRoot = config.get("web_root");
        TemplateEngine templateEngine = new ThymeleafTemplateEngineImpl(vertx, webRoot);
        // 用正则表达式判断路径是否以“.html”结尾（不区分大小写）
        router.routeWithRegex(".*\\.(?i)html").handler(routingContext -> {
            JsonObject jsonObject = new JsonObject();
            String file = routingContext.request().path();
            render(templateEngine, routingContext, jsonObject, file);
        });
    }

    @Override
    protected void setHttpServiceHandler(Map<String, String> config, Vertx vertx, Router router) {
        router.route("/service/ops_service/read_translations").handler(routingContext -> {
            HttpServerRequest request = routingContext.request();
            String language = request.getParam("language");
            if (language == null) {
                language = parseAcceptLanguageHeader(request);
                if (language != null) {
                    request.params().set("language", language);
                }
            }
            routingContext.next();
        });

        // router.route("/service/*").handler(routingContext -> {
        // String jsessionid = routingContext.session().get("jsessionid");
        // if (jsessionid != null) {
        // routingContext.request().params().set("jsessionid", jsessionid);
        // }
        // routingContext.next();
        // });

        super.setHttpServiceHandler(config, vertx, router);
    }

    private static String parseAcceptLanguageHeader(HttpServerRequest request) {
        String header = request.getHeader("accept-language");
        if (header == null)
            return null;
        StringTokenizer tokenizer = new StringTokenizer(header, ",;");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim().replace('-', '_');
            if (!token.startsWith("q=")) {
                Locale locale = ServiceConfig.instance.getLocale(token);
                if (locale != null)
                    return locale.toString();
            }
        }
        return null;
    }

    private static void render(TemplateEngine templateEngine, RoutingContext routingContext, JsonObject context,
            String templateFileName) {
        templateEngine.render(context, templateFileName).onSuccess(buffer -> {
            routingContext.response().putHeader("Content-Type", "text/html; charset=utf-8").end(buffer);
        }).onFailure(cause -> {
            routingContext.fail(cause);
        });
    }
}
