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

import java.util.Map;
import java.util.UUID;

import org.lealone.server.http.HttpRouterFactory;
import org.lealone.server.http.HttpServiceHandler;

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
        System.setProperty("vertxweb.environment", "development");
        String webRoot = config.get("web_root");
        TemplateEngine templateEngine = new PetStoreThymeleafTemplateEngine(vertx, webRoot);
        testThymeleaf(templateEngine, router);

        router.route().handler(SessionHandler.create(SessionStore.create(vertx)));

        router.route("/").handler(routingContext -> {
            routingContext.redirect("/home/index.html"); // 需要对index.html进行后端渲染
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
        router.route("/fragment/*").handler(routingContext -> {
            routingContext.fail(404);// 不允许访问Thymeleaf的fragment文件
        });

        router.route("/user/logout").handler(routingContext -> {
            // routingContext.session().remove("currentUser");
            // routingContext.redirect("/home/index.html");

            routingContext.session().remove("car_id");
        });

        // 用正则表达式判断路径是否以“.html”结尾（不区分大小写）
        router.routeWithRegex(".*\\.(?i)html").handler(routingContext -> {
            JsonObject jsonObject = new JsonObject();
            // String currentUser = routingContext.session().get("currentUser");
            // if (currentUser != null) {
            // jsonObject.put("currentUser", currentUser);
            // }
            String file = routingContext.request().path();
            templateEngine.render(jsonObject, file).onSuccess(buffer -> {
                response(routingContext, buffer);
            }).onFailure(cause -> {
                routingContext.fail(cause);
            });
        });
    }

    @Override
    protected void setHttpServiceHandler(Map<String, String> config, Vertx vertx, Router router) {
        final HttpServiceHandler serviceHandler = new HttpServiceHandler(config);
        String servicePath = getServicePath(config);
        String webRoot = config.get("web_root");
        String uploadDirectory = webRoot + "/store/img";
        router.post(servicePath).handler(BodyHandler.create(uploadDirectory));
        router.post("/service/store_service/add_product").handler(routingContext -> {
            vertx.executeBlocking(promise -> {
                for (FileUpload f : routingContext.fileUploads()) {
                    routingContext.request().params().set("logo", f.fileName());
                    routingContext.request().params().set("uploadedfilename", f.uploadedFileName());
                    // routingContext.vertx().fileSystem().delete(receiveJson.getString("filePath")
                    break;
                }
                routingContext.request().params().remove("product"); // 避免BodyHandler重复
                routingContext.next();
            });
        });

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

        router.route(servicePath).handler(routingContext -> {
            handleHttpServiceRequest(serviceHandler, routingContext);
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

    private static void testThymeleaf(TemplateEngine templateEngine, Router router) {
        router.route("/thymeleaf_hello").handler(routingContext -> {
            JsonObject jsonObject = new JsonObject().put("msg", "Hello Thymeleaf!");
            templateEngine.render(jsonObject, "/thymeleaf/hello.html").onSuccess(buffer -> {
                response(routingContext, buffer);
            }).onFailure(cause -> {
                routingContext.fail(cause);
            });
        });
        router.route("/thymeleaf_fragment").handler(routingContext -> {
            final JsonObject context = new JsonObject().put("foo", "badger").put("bar", "fox").put("context",
                    new JsonObject().put("path", "/thymeleaf/test-thymeleaf-template2.html"));
            templateEngine.render(context, "/thymeleaf/test-thymeleaf-fragmented.html").onSuccess(buffer -> {
                response(routingContext, buffer);
            }).onFailure(cause -> {
                routingContext.fail(cause);
            });
        });
    }

    private static void response(RoutingContext routingContext, Buffer buffer) {
        routingContext.response().putHeader("Content-Type", "text/html; charset=utf-8").end(buffer);
    }
}
