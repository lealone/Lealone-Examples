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
package org.lealone.opscenter.web.thymeleaf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.lealone.storage.fs.FileUtils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ThymeleafTemplateCompiler {

    public static void main(String[] args) throws IOException {
        ThymeleafTemplateCompiler compiler = new ThymeleafTemplateCompiler();
        compiler.parseArgs(args);
        compiler.compile();
    }

    private String webRoot = "./web";
    private String fragmentDirName = "fragment";
    private String targetDir = "./target";

    private ThymeleafTemplateEngineImpl te;

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
            case "-webRoot":
                webRoot = args[++i];
                break;
            case "-targetDir":
                targetDir = args[++i];
                break;
            case "-fragmentDirName":
                fragmentDirName = args[++i];
                break;
            default:
                System.out.println("选项名 '" + a + "' 无效");
                System.exit(-1);
            }
        }
    }

    private void compile() throws IOException {
        Vertx vertx = Vertx.vertx();
        te = new ThymeleafTemplateEngineImpl(vertx, this.webRoot);
        File webRoot = new File(this.webRoot);
        File targetDir = new File(this.targetDir, webRoot.getName());
        if (targetDir.exists()) {
            FileUtils.deleteRecursive(targetDir.getAbsolutePath(), true);
            targetDir.mkdir(); // 只创建子目录
        } else {
            targetDir.mkdirs(); // 创建子目录和父目录
        }

        for (File f : webRoot.listFiles()) {
            compileRecursive(targetDir, f);
        }
    }

    private void compileRecursive(File targetDir, File file) throws IOException {
        String fileName = file.getName();
        // 跳过eclipse生成的文件
        if (fileName.startsWith("."))
            return;
        if (file.isDirectory()) {
            if (fileName.equals(fragmentDirName))
                return;
            targetDir = new File(targetDir, fileName);
            targetDir.mkdir();
            for (File f : file.listFiles()) {
                compileRecursive(targetDir, f);
            }
        } else {
            fileName = fileName.toLowerCase();
            File outFile = new File(targetDir, fileName);
            if (fileName.endsWith(".html")) {
                JsonObject jsonObject = new JsonObject();
                System.out.println("compile file: " + file.getCanonicalPath() + ", to: " + outFile.getCanonicalPath());
                te.render(jsonObject, file.getAbsolutePath()).onSuccess(buffer -> {
                    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
                        out.write(buffer.getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).onFailure(cause -> {
                    cause.printStackTrace();
                });
            } else {
                try (InputStream in = new BufferedInputStream(new FileInputStream(file));
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
                    int n = 0;
                    byte[] buffer = new byte[4096];
                    while (-1 != (n = in.read(buffer))) {
                        out.write(buffer, 0, n);
                    }
                }
            }
        }
    }
}
