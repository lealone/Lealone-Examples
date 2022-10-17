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

import org.lealone.plugins.vertx.VertxRouter;

import io.vertx.core.Vertx;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class PetStoreRouter extends VertxRouter {
    @Override
    protected void initRouter(Map<String, String> config, Vertx vertx, Router router) {
        // route的顺序很重要，所以下面三个调用不能乱
        setRedirectHandler(router);
        setDevelopmentEnvironmentRouter(config, vertx, router);
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

    @Override
    protected void setHttpServiceHandler(Map<String, String> config, Vertx vertx, Router router) {
        setFileUploadHandler(config, vertx, router);
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
}
