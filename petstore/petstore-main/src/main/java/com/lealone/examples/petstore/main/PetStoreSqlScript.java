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
package com.lealone.examples.petstore.main;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class PetStoreSqlScript {

    public static void main(String[] args) throws Exception {
        new PetStoreSqlScript().run(args);
    }

    private String tableDir;
    private String serviceDir;
    private String srcDir;

    private void run(String[] args) throws Exception {
        parseArgs(args);

        // 创建PetStore数据库
        String jdbcUrl = "jdbc:lealone:tcp://localhost/lealone?user=root&password=";
        // runSql(jdbcUrl, "drop database if exists petstore");
        runSql(jdbcUrl, "create database if not exists petstore");

        // 执行建表脚本，同时自动生成对应的模型类的代码
        runScript(getSqlFile(tableDir, "tables.sql"));

        // 初始化数据
        runScript(getSqlFile(tableDir, "init-data.sql"));

        // 执行服务创建脚本，同时自动生成对应的服务接口代码
        runScript(getSqlFile(serviceDir, "services.sql"));
    }

    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
            case "-tableDir":
                tableDir = args[++i];
                break;
            case "-serviceDir":
                serviceDir = args[++i];
                break;
            case "-srcDir":
                srcDir = args[++i];
                break;
            default:
                System.out.println("选项名 '" + a + "' 无效");
                System.exit(-1);
            }
        }
    }

    private String getSqlFile(String dir, String name) {
        try {
            File file;
            if (dir != null) {
                file = new File(dir, name);
            } else {
                URL url = PetStoreSqlScript.class.getClassLoader().getResource(name);
                file = new File(url.toURI());
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void runScript(String scriptFile) throws Exception {
        String jdbcUrl = "jdbc:lealone:tcp://localhost/petstore?user=root&password=";
        String srcDir = this.srcDir;
        if (srcDir == null) {
            int pos = scriptFile.indexOf("target");
            if (pos > 0) {
                srcDir = scriptFile.substring(0, pos) + "src/main/java".replace('/', File.separatorChar);
            }
        }
        runSql(jdbcUrl, "RUNSCRIPT FROM '" + scriptFile + "'", srcDir);
    }

    private void runSql(String url, String sql) throws Exception {
        runSql(url, sql, null);
    }

    private void runSql(String url, String sql, String srcDir) throws Exception {
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            if (srcDir != null) {
                String setSql = "set @srcPath '" + srcDir + "'";
                stmt.executeUpdate(setSql);
                System.out.println("execute sql: " + setSql);
            }
            stmt.executeUpdate(sql);
            System.out.println("execute sql: " + sql);
        }
    }
}
