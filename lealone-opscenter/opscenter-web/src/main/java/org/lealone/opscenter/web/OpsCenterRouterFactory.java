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
package org.lealone.opscenter.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lealone.common.util.CaseInsensitiveMap;
import org.lealone.opscenter.web.deprecated.WebOpsHandler;
import org.lealone.opscenter.web.deprecated.WebServer;
import org.lealone.opscenter.web.thymeleaf.ThymeleafTemplateEngineImpl;
import org.lealone.server.http.HttpRouterFactory;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;

public class OpsCenterRouterFactory extends HttpRouterFactory {

    private final WebServer webServer = new WebServer();

    @Override
    protected void initRouter(Map<String, String> config, Vertx vertx, Router router) {
        int size = config.size() * 2 + 1;
        String[] args = new String[size];
        int index = 0;
        for (Map.Entry<String, String> e : config.entrySet()) {
            args[index++] = e.getKey();
            args[index++] = e.getValue();
        }
        args[index] = "-ifNotExists";
        webServer.init(args);
        org.lealone.opscenter.service.WebServer.server.init(args);

        // route的顺序很重要，所以下面三个调用不能乱
        setSessionHandler(vertx, router);
        setRedirectHandler(router);
        setDevelopmentEnvironmentRouter(config, vertx, router);
        setH2Handler(vertx, router);
    }

    protected static CaseInsensitiveMap<Object> getMethodArgs(RoutingContext routingContext) {
        CaseInsensitiveMap<Object> methodArgs = new CaseInsensitiveMap<>();
        for (Map.Entry<String, String> e : routingContext.request().params().entries()) {
            addMethodArgs(methodArgs, e.getKey(), e.getValue());
        }
        return methodArgs;
    }

    @SuppressWarnings("unchecked")
    private static void addMethodArgs(CaseInsensitiveMap<Object> methodArgs, String key, String value) {
        Object oldValue = methodArgs.get(key);
        if (oldValue != null) {
            List<String> list;
            if (oldValue instanceof String) {
                list = new ArrayList<String>();
                list.add((String) oldValue);
                methodArgs.put(key, list);
            } else {
                list = (List<String>) oldValue;
            }
            list.add(value);
        } else {
            methodArgs.put(key, value);
        }
    }

    private void setSessionHandler(Vertx vertx, Router router) {
        router.route().handler(SessionHandler.create(SessionStore.create(vertx)));
        router.route().handler(BodyHandler.create(false));
        // 只拦截logout，login直接转到UserServiceImpl.login处理了
        // 如果想处理UserServiceImpl.login的结果，可以像下面的sendHttpServiceResponse方法那样做
        router.route("/service/ops_service/logout").handler(routingContext -> {
            // routingContext.session().remove("currentUser");
            // routingContext.session().remove("car_id");
            routingContext.redirect("/ops/index.html");
        });
    }

    private void setH2Handler(Vertx vertx, Router router) {
        router.route("/").handler(routingContext -> {
            routingContext.redirect("/index.do");
        });
        // 用正则表达式判断路径是否以“.do”结尾（不区分大小写）
        router.routeWithRegex(".*\\.(?i)do").handler(routingContext -> {
            try {
                new WebOpsHandler(webServer).process(routingContext, getMethodArgs(routingContext));
            } catch (IOException e) {
                routingContext.failed();
            }
        });

        router.routeWithRegex(".*\\.(?i)jsp").handler(routingContext -> {
            try {
                new WebOpsHandler(webServer).process(routingContext, getMethodArgs(routingContext));
            } catch (IOException e) {
                routingContext.failed();
            }
        });
        router.route().handler(routingContext -> {
            String file = routingContext.request().path();
            if (file.startsWith("/ops/") || file.startsWith("/common/") || file.startsWith("/admin/")
                    || file.startsWith("/service/"))
                routingContext.next();
            else if (file.endsWith(".css"))
                routingContext.redirect("/common/css/opscenter.css");
            else if (file.endsWith(".js"))
                routingContext.redirect("/ops/js" + file);
            else
                routingContext.redirect("/ops/img" + file);
        });
    }

    @Override
    protected void sendHttpServiceResponse(RoutingContext routingContext, String serviceName, String methodName,
            Buffer result) {
        // if ("user_service".equalsIgnoreCase(serviceName) && "login".equalsIgnoreCase(methodName)) {
        // JsonObject receiveJson = new JsonObject(result);
        // routingContext.session().put("currentUser", receiveJson.getValue("USER_ID"));
        // }
        super.sendHttpServiceResponse(routingContext, serviceName, methodName, result);
    }

    private void setRedirectHandler(Router router) {
        router.route("/").handler(routingContext -> {
            routingContext.redirect("/index.do");
        });
        router.route("/ops").handler(routingContext -> {
            routingContext.redirect("/ops/index.html");
        });
        router.route("/admin").handler(routingContext -> {
            routingContext.redirect("/admin/index.html");
        });
    }

    private void setDevelopmentEnvironmentRouter(Map<String, String> config, Vertx vertx, Router router) {
        if (!isDevelopmentEnvironment(config))
            return;

        System.setProperty("vertxweb.environment", "development");
        router.route("/fragment/*").handler(routingContext -> {
            routingContext.fail(404);// 不允许访问Thymeleaf的fragment文件
        });

        String webRoot = config.get("web_root");
        TemplateEngine templateEngine = new ThymeleafTemplateEngineImpl(vertx, webRoot);
        // 用正则表达式判断路径是否以“.html”结尾（不区分大小写）
        router.routeWithRegex(".*\\.(?i)html").handler(routingContext -> {
            JsonObject jsonObject = new JsonObject();
            // String currentUser = routingContext.session().get("currentUser");
            // if (currentUser != null) {
            // jsonObject.put("currentUser", currentUser);
            // }
            String file = routingContext.request().path();
            render(templateEngine, routingContext, jsonObject, file);
        });
    }

    @Override
    protected void setHttpServiceHandler(Map<String, String> config, Vertx vertx, Router router) {
        setFileUploadHandler(config, vertx, router);

        // 提取购物车ID用于调用后续的购物车服务
        router.route("/service/car_service/*").handler(routingContext -> {
            String car = routingContext.session().get("car_id");
            if (car == null) {
                car = "car-" + UUID.randomUUID();
                routingContext.session().put("car_id", car);
            }
            routingContext.request().params().set("car_id", car);
            routingContext.next();
        });

        super.setHttpServiceHandler(config, vertx, router);
    }

    private void setFileUploadHandler(Map<String, String> config, Vertx vertx, Router router) {
        String uploadDirectory = config.get("upload_directory");
        if (uploadDirectory == null)
            uploadDirectory = config.get("web_root") + "/store/img/file_uploads";
        BodyHandler bodyHandler = BodyHandler.create(uploadDirectory);
        // 先不合并，留给父类setHttpServiceHandler中定义的BodyHandler合并，否则表单属性会重复
        bodyHandler.setMergeFormAttributes(false);
        router.post("/service/store_service/add_product").handler(bodyHandler);
        router.post("/service/store_service/add_product").handler(routingContext -> {
            for (FileUpload f : routingContext.fileUploads()) {
                routingContext.request().params().set("logo", f.fileName());
                File logoFile = new File(config.get("web_root") + "/store/img/", f.fileName());
                File uploadedFile = new File(f.uploadedFileName());
                uploadedFile.renameTo(logoFile);
                break;
            }
            routingContext.next();
        });
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
