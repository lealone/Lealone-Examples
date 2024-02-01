#!/bin/sh
mvn package -Dmaven.test.skip=true
java -cp petstore-dist/target/lealone-petstore-1.0.0.jar com.lealone.plugins.service.template.TemplateCompiler -webRoot petstore-web/web -targetDir petstore-dist/target
mvn assembly:assembly -Dmaven.test.skip=true
