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
package org.lealone.examples.petstore.web;

import java.io.File;
import java.util.Map;
import java.util.UUID;

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

public class PetStoreRouterFactory extends HttpRouterFactory {
    @Override
    protected void initRouter(Map<String, String> config, Vertx vertx, Router router) {
        // route的顺序很重要，所以下面三个调用不能乱
        setSessionHandler(vertx, router);
        setRedirectHandler(router);
        setDevelopmentEnvironmentRouter(config, vertx, router);
    }

    private void setSessionHandler(Vertx vertx, Router router) {
        router.route().handler(SessionHandler.create(SessionStore.create(vertx)));
        // 只拦截logout，login直接转到UserServiceImpl.login处理了
        // 如果想处理UserServiceImpl.login的结果，可以像下面的sendHttpServiceResponse方法那样做
        router.route("/user/logout").handler(routingContext -> {
            // routingContext.session().remove("currentUser");
            // routingContext.session().remove("car_id");
            routingContext.redirect("/home/index.html");
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
            routingContext.redirect("/home/index.html");
        });
        router.route("/user").handler(routingContext -> {
            routingContext.redirect("/user/index.html");
        });
        router.route("/store").handler(routingContext -> {
            routingContext.redirect("/store/index.html");
        });
        // 实现footer.html中的重定向功能
        router.route("/redirect.do").handler(routingContext -> {
            routingContext.redirect(routingContext.request().getParam("location"));
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
        TemplateEngine templateEngine = new PetStoreThymeleafTemplateEngine(vertx, webRoot);
        testThymeleaf(templateEngine, router);
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
                File logo = new File(config.get("web_root") + "/store/img/", f.fileName());
                File uploadedFileName = new File(f.uploadedFileName());
                uploadedFileName.renameTo(logo);
                break;
            }
            routingContext.next();
        });
    }

    private static void testThymeleaf(TemplateEngine templateEngine, Router router) {
        router.route("/thymeleaf_hello").handler(routingContext -> {
            JsonObject jsonObject = new JsonObject().put("msg", "Hello Thymeleaf!");
            render(templateEngine, routingContext, jsonObject, "/thymeleaf/hello.html");
        });
        router.route("/thymeleaf_fragment").handler(routingContext -> {
            JsonObject jsonObject = new JsonObject().put("foo", "badger").put("bar", "fox").put("context",
                    new JsonObject().put("path", "/thymeleaf/test-thymeleaf-template2.html"));
            render(templateEngine, routingContext, jsonObject, "/thymeleaf/test-thymeleaf-fragmented.html");
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
