@REM
@REM  Licensed to the Apache Software Foundation (ASF) under one or more
@REM  contributor license agreements.  See the NOTICE file distributed with
@REM  this work for additional information regarding copyright ownership.
@REM  The ASF licenses this file to You under the Apache License, Version 2.0
@REM  (the "License"); you may not use this file except in compliance with
@REM  the License.  You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.

@echo off
if "%OS%" == "Windows_NT" setlocal

set ARG=%1

if /i "%ARG%" == "" goto usage
if /i "%ARG%" == "-e" goto e
if /i "%ARG%" == "-ec" goto ec
if /i "%ARG%" == "-es" goto es
if /i "%ARG%" == "-p" goto p
if /i "%ARG%" == "-pc" goto pc
if /i "%ARG%" == "-i" goto i
if /i "%ARG%" == "-c" goto c
if /i "%ARG%" == "-dt" goto dt
if /i "%ARG%" == "-vu" goto vu
if /i "%ARG%" == "-r" goto r

goto usage

:usage
echo usage: build [options]
echo    options:
echo    -e            mvn eclipse:eclipse
echo    -ec           mvn eclipse:clean
echo    -es           mvn eclipse:eclipse -DdownloadSources=true
echo    -p            mvn package -Dmaven.test.skip=true
echo    -pc           mvn clean package -Dmaven.test.skip=true
echo    -i            mvn install -Dmaven.test.skip=true
echo    -c            mvn clean
echo    -dt           mvn dependency:tree
echo    -vu version   pom.xml version update
echo    -r            java -jar ./target/lealone-python-demo-all-1.0.0.jar
goto end

:e
call mvn eclipse:eclipse
goto end

:ec
call mvn eclipse:clean
goto end

:es
call mvn eclipse:eclipse -DdownloadSources=true
goto end

:p
call mvn package -Dmaven.test.skip=true
goto end

:pc
call mvn clean package -Dmaven.test.skip=true
goto end

:c
call mvn clean
goto end

:i
call mvn install -Dmaven.test.skip=true
goto end

:dt
call mvn dependency:tree
goto end

:vu
set VERSION=%2
if /i "%VERSION%" == "" goto usage
call mvn versions:set -DnewVersion=%VERSION%
call mvn versions:commit
goto end

:r
java -jar ./target/lealone-python-demo-all-1.0.0.jar
:end
