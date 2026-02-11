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
package com.lealone.examples.petstore.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.lealone.plugins.service.template.TemplateEngine;
import com.lealone.plugins.tomcat.TomcatRouter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class PetStoreRouter extends TomcatRouter {

    private String webRoot;
    // private String uploadDirectory;

    @Override
    public void init(Map<String, String> config) {
        webRoot = config.get("web_root");
        // String uploadDirectory = config.get("upload_directory");
        // if (uploadDirectory == null)
        // uploadDirectory = config.get("web_root") + "/store/img/file_uploads";
        // this.uploadDirectory = uploadDirectory;

        addFilter("redirectFilter", new RedirectFilter(), "/*");
        if (isDevelopmentEnvironment(config)) {
            addFilter("templateFilter", new TemplateFilter(), "*.html");
        }
        addFilter("fileUploadFilter", new FileUploadFilter(), "/service/store_service/add_product");
        tomcatServer.getContext().setAllowCasualMultipartParsing(true);
        super.init(config);
    }

    private boolean isDevelopmentEnvironment(Map<String, String> config) {
        String environment = config.get("environment");
        if (environment != null) {
            environment = environment.trim().toLowerCase();
            if (environment.equals("development") || environment.equals("dev")) {
                return true;
            }
        }
        return false;
    }

    private class TemplateFilter extends HttpFilter {
        @Override
        protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            TemplateEngine te = new TemplateEngine(webRoot, "utf-8");
            String file = request.getRequestURI();
            try {
                String str = te.process(file);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=utf-8");
                response.getWriter().write(str);
            } catch (Exception e) {
            }
        }
    }

    private class RedirectFilter extends HttpFilter {
        @Override
        protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            String url = request.getRequestURI();
            if (url.equals("/")) {
                response.sendRedirect("/home/index.html");
            } else if (url.equals("/user")) {
                response.sendRedirect("/user/index.html");
            } else if (url.equals("/store")) {
                response.sendRedirect("/store/index.html");
            } else if (url.equals("/redirect.do")) {
                response.sendRedirect(request.getParameter("location"));
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private class FileUploadFilter extends HttpFilter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        protected void doFilter(HttpServletRequest request, HttpServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            for (Part p : request.getParts()) {
                // request.getParameterMap().put("logo", new String[] { p.getName() });
                File logoFile = new File(webRoot + "/store/img/", p.getName());
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(logoFile));
                IOUtils.copy(p.getInputStream(), out);
                out.close();
            }
            chain.doFilter(request, response);
        }
    }
}
