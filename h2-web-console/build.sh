#!/bin/sh
mvn package -Dmaven.test.skip=true
java -cp h2webconsole-dist/target/h2webconsole-1.0.0.jar org.lealone.examples.h2webconsole.web.thymeleaf.ThymeleafTemplateCompiler -webRoot h2webconsole-web/web -targetDir h2webconsole-dist/target
mvn assembly:assembly -Dmaven.test.skip=true

