#!/bin/sh
mvn package -Dmaven.test.skip=true
java -cp opscenter-dist/target/lealone-opscenter-1.0.0.jar org.lealone.opscenter.web.thymeleaf.ThymeleafTemplateCompiler -webRoot opscenter-web/web -targetDir opscenter-dist/target
mvn assembly:assembly -Dmaven.test.skip=true

